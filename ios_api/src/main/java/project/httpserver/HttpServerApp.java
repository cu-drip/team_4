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

public class HttpServerApp {

    // Method to get the local IP address
    public static String getLocalIp() {
        try {
            // Get all network interfaces
            Enumeration<NetworkInterface> interfaces = NetworkInterface.getNetworkInterfaces();
            while (interfaces.hasMoreElements()) {
                NetworkInterface networkInterface = interfaces.nextElement();
                Enumeration<java.net.InetAddress> addresses = networkInterface.getInetAddresses();
                while (addresses.hasMoreElements()) {
                    java.net.InetAddress address = addresses.nextElement();
                    
                    // Check if it's an IPv4 and local address
                    if (address.isSiteLocalAddress() && address instanceof java.net.Inet4Address) {
                        return address.getHostAddress();
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        return null; // Return null if no IP address is found
    }

    // Method to start the HTTP server
    public static void startHttpServer() throws Exception {
        // Get the local IP address
        String localIp = getLocalIp();
        if (localIp == null) {
            System.out.println("Local IP address not found.");
            return;
        }

        // Create HTTP server on the found IP address and port 8080
        HttpServer server = HttpServer.create(new InetSocketAddress(localIp, 8080), 0);

        // Create contexts and bind handlers
        server.createContext("/json", new JsonHandler());
        server.createContext("/greeting", new GreetingHandler());
        server.createContext("/tournament", new TournamentHandler()); // TournamentHandler moved to a separate file

        // Set default executor and start the server
        server.setExecutor(null);
        server.start();
        System.out.println("HTTP Server started on " + localIp + ":8080");
    }

    // HTTP handler for processing JSON POST requests
    static class JsonHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("POST".equals(exchange.getRequestMethod())) {
                // Read the request body
                InputStream body = exchange.getRequestBody();
                InputStreamReader reader = new InputStreamReader(body, "UTF-8");
                BufferedReader bufferedReader = new BufferedReader(reader);
                StringBuilder requestBody = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    requestBody.append(line);
                }

                // Convert the request body to JSON
                JsonObject receivedJson = new JsonObject();
                try {
                    // Try to parse the received data as JSON
                    receivedJson = new com.google.gson.JsonParser().parse(requestBody.toString()).getAsJsonObject();
                } catch (Exception e) {
                    // If parsing fails, return an error message
                    receivedJson.addProperty("error", "Invalid JSON format");
                }

                // Log the received JSON
                System.out.println("Received JSON: " + receivedJson);

                // Create response
                String response = receivedJson.toString();

                // Set response headers and send the response
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

    // HTTP handler for processing greeting GET requests
    static class GreetingHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            if ("GET".equals(exchange.getRequestMethod())) {
                // Create a greeting message as JSON
                JsonObject responseJson = new JsonObject();
                responseJson.addProperty("message", "Hello, welcome to the HTTP server!");

                String response = responseJson.toString();

                // Set response headers and send the response
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
        // Start the HTTP server
        startHttpServer();
    }
}
