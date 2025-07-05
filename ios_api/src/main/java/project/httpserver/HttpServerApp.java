package project.httpserver;

import java.net.InetSocketAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;
import java.io.OutputStream;

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

        // Create context for tournament handler
        server.createContext("/tournament", new TournamentHandler());
        
        // Create context for /user/{id} endpoint
        server.createContext("/user/", new UserHandler());  // Note the trailing slash

        // Set default executor and start the server
        server.setExecutor(null);
        server.start();
        System.out.println("HTTP Server started on " + localIp + ":8080");
    }

    public static void main(String[] args) throws Exception {
        // Start the HTTP server
        startHttpServer();
    }
}
