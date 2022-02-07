package com.server;

import com.sun.net.httpserver.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class Server implements HttpHandler {
    ArrayList<String> messages = new ArrayList<>();

    private Server() {
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        
        if (t.getRequestMethod().equalsIgnoreCase("POST")) {
            InputStream stream = t.getRequestBody();
            
            String text = new BufferedReader(new InputStreamReader(stream,
            StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
            
            messages.add(text);
            stream.close();
            t.sendResponseHeaders(200, -1);

        } else if (t.getRequestMethod().equalsIgnoreCase("GET")) {
            String reponseCoordinates = "";

            if (messages.isEmpty()) {
                reponseCoordinates = "No coordinates\n";
            } else {
                for (String coordinate : messages) {
                    reponseCoordinates = reponseCoordinates.concat(coordinate);
                    reponseCoordinates += "\n";
                }
            }

            byte [] bytes = reponseCoordinates.getBytes(StandardCharsets.UTF_8);
            t.sendResponseHeaders(200, bytes.length);
            OutputStream messageBodyStream = t.getResponseBody();
            messageBodyStream.write(bytes);
            messageBodyStream.close();

        } else {
            String reponse = "Not supported\n";

            byte [] bytes = reponse.getBytes(StandardCharsets.UTF_8);
            t.sendResponseHeaders(400, bytes.length);
            OutputStream messageBodyStream = t.getResponseBody();
            messageBodyStream.write(bytes);
            messageBodyStream.close();
        }
    }


    public static void main(String[] args) throws Exception {
        //create the http server to port 8001 with default logger
        HttpServer server = HttpServer.create(new InetSocketAddress(8001),0);
        //create context that defines path for the resource, in this case a "help"
        server.createContext("/coordinates", new Server());
        // creates a default executor
        server.setExecutor(null);
        server.start();
    }
}
