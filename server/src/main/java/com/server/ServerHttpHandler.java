package com.server;

import com.sun.net.httpserver.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.stream.Collectors;

public class ServerHttpHandler implements HttpHandler {
    ArrayList<String> messages = new ArrayList<>();

    public ServerHttpHandler() {
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
}
