package com.server;

import com.sun.net.httpserver.*;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.stream.Collectors;

public class RegistrationHandler implements HttpHandler {
    private final UserAuthenticator authenticator;

    public RegistrationHandler(UserAuthenticator authenticator) {
        this.authenticator = authenticator;
    }

    @Override
    public void handle(HttpExchange t) throws IOException {
        if (t.getRequestMethod().equalsIgnoreCase("POST")) {
            Headers headers = t.getRequestHeaders();
            String contentType;
            if (headers.containsKey("Content-Type")) {
                contentType = headers.get("Content-Type").get(0);

                if (contentType.equals("application/json")) {
                    InputStream stream = t.getRequestBody();
                
                    String jsonString = new BufferedReader(new InputStreamReader(stream,
                    StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));
    
                    try {
                        JSONObject userJSON = new JSONObject(jsonString);
                        String username = userJSON.getString("username");
                        String password = userJSON.getString("password");
                        String email = userJSON.getString("email");
    
                        if (username.length() == 0 || password.length() == 0 || email.length() == 0) {
                            String reponse = "User info not proper\n";
    
                            byte [] bytes = reponse.getBytes(StandardCharsets.UTF_8);
                            t.sendResponseHeaders(400, bytes.length);
                            OutputStream messageBodyStream = t.getResponseBody();
                            messageBodyStream.write(bytes);
                            messageBodyStream.close();
                        }
                        else if (!(authenticator.addUser(username, password, email))) {
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
                    } catch (JSONException e) {
                        String reponse = "Error when parsing JSON object\n";
                        byte [] bytes = reponse.getBytes(StandardCharsets.UTF_8);
                        t.sendResponseHeaders(400, bytes.length);
                        OutputStream messageBodyStream = t.getResponseBody();
                        messageBodyStream.write(bytes);
                        messageBodyStream.close();
                    }
                } else {
                    String reponse = "Wrong content type. Only JSON is supported\n";
    
                    byte [] bytes = reponse.getBytes(StandardCharsets.UTF_8);
                    t.sendResponseHeaders(400, bytes.length);
                    OutputStream messageBodyStream = t.getResponseBody();
                    messageBodyStream.write(bytes);
                    messageBodyStream.close();
                } 
            } else {
                String reponse = "No content type available\n";
    
                byte [] bytes = reponse.getBytes(StandardCharsets.UTF_8);
                t.sendResponseHeaders(400, bytes.length);
                OutputStream messageBodyStream = t.getResponseBody();
                messageBodyStream.write(bytes);
                messageBodyStream.close();
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
