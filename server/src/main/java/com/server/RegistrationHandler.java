package com.server;

import com.sun.net.httpserver.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class RegistrationHandler implements HttpHandler {
    private UserAuthenticator authenticator;

    public RegistrationHandler(UserAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        if (t.getRequestMethod().equalsIgnoreCase("POST")) {
            InputStream stream = t.getRequestBody();
            
            String text = new BufferedReader(new InputStreamReader(stream,
            StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
            
            String [] userInfo = text.split(":");
            if (userInfo.length != 2) {
                stream.close();
                t.sendResponseHeaders(400, -1);
            } else if (!(authenticator.addUser(userInfo[0], userInfo[1]))) {
                String reponse = "User already registered\n";

                byte [] bytes = reponse.getBytes(StandardCharsets.UTF_8);
                t.sendResponseHeaders(403, bytes.length);
                OutputStream messageBodyStream = t.getResponseBody();
                messageBodyStream.write(bytes);
                messageBodyStream.close();
            } else {
                stream.close();
                t.sendResponseHeaders(200, -1);
            }
            
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
