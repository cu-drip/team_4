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
import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import java.net.URI;

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

        // Обработчик для GET-запросов на /greeting
        server.createContext("/greeting", new GreetingHandler());

        // Обработчик для GET-запросов, требующих получения JSON с полями таблицы tournaments
        server.createContext("/tournament", new TournamentHandler());

        // Стандартный executor
        server.setExecutor(null);
        server.start();
        System.out.println("HTTP Server started on " + localIp + ":8080");
    }

    // HTTP-обработчик для обработки JSON-запросов
    static class JsonHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
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
                exchange.sendResponseHeaders(200, response.getBytes().length);

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                String response = "Only POST method is allowed";
                exchange.sendResponseHeaders(405, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    // HTTP-обработчик для GET-запросов с параметрами
    static class TournamentHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            System.out.println("Request received at /tournament: " + exchange.getRequestURI());
            if ("GET".equals(exchange.getRequestMethod())) {
                // Создаем список JSON-объектов с полями для таблицы tournament
                JsonObject responseJson1 = new JsonObject();
                responseJson1.addProperty("id", "123e4567-e89b-12d3-a456-426614174000");
                responseJson1.addProperty("title", "Example Tournament");
                responseJson1.addProperty("description", "This is an example tournament description.");
                responseJson1.addProperty("sport", "Football");
                responseJson1.addProperty("type_tournament", "Knockout");
                responseJson1.addProperty("type_group", "Single Elimination");
                responseJson1.addProperty("matches_number", "16");
                responseJson1.addProperty("start_time", "2025-08-01T10:00:00");
                responseJson1.addProperty("created_at", "2025-06-01T12:00:00");
                responseJson1.addProperty("entry_cost", "20.00");
                responseJson1.addProperty("max_participants", "32");
                responseJson1.addProperty("registration_deadline", "2025-07-15T23:59:59");
                responseJson1.addProperty("place", "Stadium A");
                responseJson1.addProperty("organizer_id", "123e4567-e89b-12d3-a456-426614174001");
    
                // Второй турнирный объект
                JsonObject responseJson2 = new JsonObject();
                responseJson2.addProperty("id", "223e4567-e89b-12d3-a456-426614174000");
                responseJson2.addProperty("title", "Summer Cup 2025");
                responseJson2.addProperty("description", "A summer cup tournament with regional teams.");
                responseJson2.addProperty("sport", "Basketball");
                responseJson2.addProperty("type_tournament", "Round Robin");
                responseJson2.addProperty("type_group", "Pool Play");
                responseJson2.addProperty("matches_number", "32");
                responseJson2.addProperty("start_time", "2025-07-15T09:00:00");
                responseJson2.addProperty("created_at", "2025-05-10T08:00:00");
                responseJson2.addProperty("entry_cost", "15.00");
                responseJson2.addProperty("max_participants", "64");
                responseJson2.addProperty("registration_deadline", "2025-07-10T23:59:59");
                responseJson2.addProperty("place", "Arena B");
                responseJson2.addProperty("organizer_id", "223e4567-e89b-12d3-a456-426614174001");

                responseJson1 = JsonConverter.convertToStringJsonObject(responseJson1);
                responseJson2 = JsonConverter.convertToStringJsonObject(responseJson2);
    
                // Массив JSON-объектов
                JsonObject[] tournaments = {responseJson1, responseJson2};
    
                // Преобразуем массив в JSON
                StringBuilder response = new StringBuilder("[");
                for (int i = 0; i < tournaments.length; i++) {
                    response.append(tournaments[i].toString());
                    if (i < tournaments.length - 1) {
                        response.append(",");
                    }
                }
                response.append("]");
    
                // Устанавливаем заголовки и отправляем ответ
                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.toString().getBytes().length);
    
                OutputStream os = exchange.getResponseBody();
                os.write(response.toString().getBytes());
                os.close();
            } else {
                String response = "Only GET method is allowed";
                exchange.sendResponseHeaders(405, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    // HTTP-обработчик для GET-запросов
    static class GreetingHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("message", "Hello, welcome to the HTTP server!");

                String response = responseJson.toString();

                exchange.getResponseHeaders().set("Content-Type", "application/json");
                exchange.sendResponseHeaders(200, response.getBytes().length);

                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            } else {
                String response = "Only GET method is allowed";
                exchange.sendResponseHeaders(405, response.getBytes().length);
                OutputStream os = exchange.getResponseBody();
                os.write(response.getBytes());
                os.close();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        startHttpServer();
    }
}
