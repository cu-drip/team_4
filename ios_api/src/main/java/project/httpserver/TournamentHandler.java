package project;

import java.io.IOException;
import java.io.OutputStream;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

public class TournamentHandler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Request received at /tournament: " + exchange.getRequestURI());
        
        if ("GET".equals(exchange.getRequestMethod())) {
            // Creating JSON objects for tournament data
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

            // Second tournament object
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

            // Array of JSON objects for tournaments
            JsonObject[] tournaments = {responseJson1, responseJson2};

            // Constructing the JSON array response
            StringBuilder response = new StringBuilder("[");
            for (int i = 0; i < tournaments.length; i++) {
                response.append(tournaments[i].toString());
                if (i < tournaments.length - 1) {
                    response.append(",");
                }
            }
            response.append("]");

            // Send the response headers and body
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
