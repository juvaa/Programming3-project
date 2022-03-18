package com.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.format.DateTimeParseException;
import java.util.stream.Collectors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.json.JSONException;
import org.json.JSONObject;


public class CommentHandler implements HttpHandler {
    
    /* TODO: Refactor this whole method */
    @Override
    public void handle(HttpExchange t) throws IOException {
        CoordinateDatabase db = CoordinateDatabase.getInstance();

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
                        JSONObject commentJSON = new JSONObject(jsonString);

                        int coordinateId = commentJSON.getInt("id");
                        String comment = commentJSON.getString("comment");
                        String timestampString = commentJSON.getString("sent");
                        
                        if (coordinateId <= 0 || comment.length() == 0 ||
                                timestampString.length() == 0) {
                            String reponse = "Coordinate info not proper\n";
    
                            byte [] bytes = reponse.getBytes(StandardCharsets.UTF_8);
                            t.sendResponseHeaders(400, bytes.length);
                            OutputStream messageBodyStream = t.getResponseBody();
                            messageBodyStream.write(bytes);
                            messageBodyStream.close();
                        } else {
                            db.setComment(
                                new CoordinateComment(coordinateId, comment, timestampString));
                            
                            stream.close();
                            t.sendResponseHeaders(200, -1);
                        }
                    } catch (JSONException | DateTimeParseException | SQLException e) {
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
