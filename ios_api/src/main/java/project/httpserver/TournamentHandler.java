package project.httpserver;

import java.io.IOException;
import java.io.OutputStream;

import project.JsonConverter;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import project.HttpJsonClient;


public class TournamentHandler implements HttpHandler {

    private static final String urlString = "http://localhost:8080/api/v1/tournaments";

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        System.out.println("Request received at /tournament: " + exchange.getRequestURI());
     
        if ("GET".equals(exchange.getRequestMethod())) {
            // Creating JSON objects for tournament data
            JsonObject responseJson1 = null;
            try {
                responseJson1 = HttpJsonClient.sendGetRequest(urlString);
            } catch (Exception ex) {
            }

            // Second tournament object
            JsonObject responseJson2 = null;
            try {
                responseJson2 = HttpJsonClient.sendGetRequest(urlString);
            } catch (Exception ex) {
            }

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
