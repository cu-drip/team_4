package project;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpJsonClient {

    // Метод для выполнения GET-запроса и получения JSON-ответа
    public static JsonObject sendGetRequest(String urlString) throws Exception {
        // Создаем объект URL для переданного адреса
        URL url = new URL(urlString);
        
        // Открываем соединение
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        // Устанавливаем метод запроса GET
        connection.setRequestMethod("GET");
        
        // Устанавливаем таймауты
        connection.setConnectTimeout(5000); // 5 секунд на установление соединения
        connection.setReadTimeout(5000);    // 5 секунд на чтение данных
        
        // Получаем код ответа
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new Exception("HTTP GET Request failed with response code: " + responseCode);
        }
        
        // Читаем ответ от сервера
        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        String inputLine;
        StringBuilder response = new StringBuilder();
        
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        
        // Преобразуем строку ответа в JSON объект
        JsonObject jsonResponse = JsonParser.parseString(response.toString()).getAsJsonObject();
        
        return jsonResponse;
    }

    // Пример использования
    public static void main(String[] args) {
        try {
            // URL для отправки GET запроса
            String url = "https://api.example.com/data"; // Замените на настоящий URL

            // Отправляем GET-запрос и получаем JSON-ответ
            JsonObject jsonResponse = sendGetRequest(url);

            // Выводим полученный JSON
            System.out.println("Полученный JSON:");
            System.out.println(jsonResponse);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
