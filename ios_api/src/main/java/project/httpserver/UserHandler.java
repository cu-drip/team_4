package project.httpserver;

import java.io.IOException;
import java.io.OutputStream;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import project.JsonConverter;

public class UserHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        // Extract user ID from the URL path
        String requestURI = exchange.getRequestURI().toString();
        String userId = requestURI.substring("/user/".length()); // Extracts the ID from /user/{id}

        System.out.println("Request for user ID: " + userId);
        
        // Create two user JSON objects
        JsonObject user1 = new JsonObject();
        user1.addProperty("id", "123e4567-e89b-12d3-a456-426614174000");
        user1.addProperty("name", "John");
        user1.addProperty("surname", "Doe");
        user1.addProperty("email", "john.doe@example.com");
        user1.addProperty("hashed_password", "$2b$12$abcdefg");
        user1.addProperty("is_admin", true);
        user1.addProperty("date_of_birth", "1990-05-15");
        user1.addProperty("age", 35);
        user1.addProperty("sex", "male");
        user1.addProperty("weight", 75.5);
        user1.addProperty("height", 180);
        user1.addProperty("mmr", 1500.5);
        user1.addProperty("created_at", "2023-01-01T10:00:00Z");
        user1.addProperty("bio", "A passionate gamer.");
        user1.addProperty("is_organizer", true);
        user1.addProperty("avatar_url", "https://example.com/avatar1.jpg");

        JsonObject user2 = new JsonObject();
        user2.addProperty("id", "223e4567-e89b-12d3-a456-426614174001");
        user2.addProperty("name", "Jane");
        user2.addProperty("surname", "Smith");
        user2.addProperty("email", "jane.smith@example.com");
        user2.addProperty("hashed_password", "$2b$12$hijklmnop");
        user2.addProperty("is_admin", false);
        user2.addProperty("date_of_birth", "1995-11-22");
        user2.addProperty("age", 30);
        user2.addProperty("sex", "female");
        user2.addProperty("weight", 65.3);
        user2.addProperty("height", 170);
        user2.addProperty("mmr", 1400.2);
        user2.addProperty("created_at", "2023-02-10T14:00:00Z");
        user2.addProperty("bio", "Love playing strategy games.");
        user2.addProperty("is_organizer", false);
        user2.addProperty("avatar_url", "https://example.com/avatar2.jpg");

        user1 = JsonConverter.convertToStringJsonObject(user1);
        user2 = JsonConverter.convertToStringJsonObject(user2);
        
        String responseJson;

        // Check if the userId matches either of the two IDs
        if ("123e4567-e89b-12d3-a456-426614174000".equals(userId)) {
            responseJson = user1.toString();  // Return user1 JSON
        } else if ("223e4567-e89b-12d3-a456-426614174001".equals(userId)) {
            responseJson = user2.toString();  // Return user2 JSON
        } else {
            // If the user ID is not found, return an error
            JsonObject errorResponse = new JsonObject();
            errorResponse.addProperty("error", "User ID not found");
            responseJson = errorResponse.toString();
            exchange.sendResponseHeaders(404, responseJson.getBytes().length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseJson.getBytes());
            os.close();
            return;
        }

        // Set response headers and send response
        exchange.getResponseHeaders().set("Content-Type", "application/json");
        exchange.sendResponseHeaders(200, responseJson.getBytes().length);

        OutputStream os = exchange.getResponseBody();
        os.write(responseJson.getBytes());
        os.close();
    }
}
