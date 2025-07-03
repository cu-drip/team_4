package project;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

public class HttpServerApp {

    // Получаем локальный IP-адрес
    public static String getLocalIp() {
        try {
            // Получаем все сетевые интерфейсы
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<java.net.InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    java.net.InetAddress address = addresses.nextElement();
                    
                    // Проверяем, что это IPv4 и локальный адрес
                    if (address.isSiteLocalAddress() && address instanceof java.net.Inet4Address) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null; // Если IP не найден
    }    

    // Запуск HTTP-сервера
    public static void startHttpServer() throws Exception {
        // Получаем локальный IP-адрес
        String localIp = getLocalIp();
        if (localIp == null) {
            System.out.println("Local IP address not found.");
            return;
        }

        // Создаем сервер на найденном IP-адресе и порту 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(localIp, 8080), 0);

        // Обработчик для запроса /json
        server.createContext("/json", new JsonHandler());

        // Создаем пул потоков для обработки запросов
        ExecutorService executor = Executors.newCachedThreadPool();
        server.setExecutor(executor);
        server.start();
        System.out.println("HTTP Server started on " + localIp + ":8080");
    }

    // HTTP-обработчик для обработки JSON-запросов
    static class JsonHandler implements HttpHandler {
        private static final Random random = new Random();

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String requestMethod = exchange.getRequestMethod();

            // Если POST, то обрабатываем запрос
            if ("POST".equals(requestMethod)) {
                handlePostRequest(exchange);
            } else if ("GET".equals(requestMethod)) {
                handleGetRequest(exchange);
            } else {
                // Для других методов - 405
                String response = "Only POST and GET methods are allowed";
                exchange.sendResponseHeaders(405, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }

        private void handlePostRequest(HttpExchange exchange) throws IOException {
            // Чтение тела запроса
            InputStream body = exchange.getRequestBody();
            InputStreamReader reader = new InputStreamReader(body, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(reader);
            StringBuilder requestBody = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                requestBody.append(line);
            }

            // Преобразуем строку в JSON
            JsonObject receivedJson = new JsonObject();
            try {
                // Попробуем распарсить полученные данные как JSON
                receivedJson = new com.google.gson.JsonParser().parse(requestBody.toString()).getAsJsonObject();
            } catch (Exception e) {
                // Если ошибка при парсинге - отправляем ошибку
                receivedJson.addProperty("error", "Invalid JSON format");
            }

            // Логируем полученный JSON
            System.out.println("Received JSON: " + receivedJson);

            // Формируем ответ
            String response = receivedJson.toString();

            // Устанавливаем заголовки и отправляем ответ
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Connection", "keep-alive");  // Чтобы подключение оставалось открытым
            exchange.sendResponseHeaders(200, response.getBytes().length);

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }

        private void handleGetRequest(HttpExchange exchange) throws IOException {
            // Генерация случайного JSON-ответа
            JsonObject responseJson = new JsonObject();
            int responseType = random.nextInt(3); // 0, 1, 2 для различных типов ответа

            switch (responseType) {
                case 0:
                    responseJson.addProperty("message", "Random response 1");
                    break;
                case 1:
                    responseJson.addProperty("status", "OK");
                    responseJson.addProperty("code", 200);
                    break;
                case 2:
                    responseJson.addProperty("error", "Something went wrong");
                    responseJson.addProperty("code", 500);
                    break;
            }

            String response = responseJson.toString();

            // Устанавливаем заголовки и отправляем ответ
            exchange.getResponseHeaders().set("Content-Type", "application/json");
            exchange.getResponseHeaders().set("Connection", "keep-alive");  // Чтобы подключение оставалось открытым
            exchange.sendResponseHeaders(200, response.getBytes().length);

            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    public static void main(String[] args) throws Exception {
        startHttpServer();
    }
}
