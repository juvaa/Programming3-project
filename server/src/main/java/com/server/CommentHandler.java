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
    
    @Override
    public void handle(HttpExchange t) throws IOException {
        if (t.getRequestMethod().equalsIgnoreCase("POST")) {
            handlePost(t);
        } else {
            String response = "Not supported\n";

            sendResponse(response, 400, t);
        }
    }

   /* TODO: Maybe refactor the if-else try-catch spaghetti */
    private void handlePost(HttpExchange t) throws IOException {
        CoordinateDatabase db = CoordinateDatabase.getInstance();
        Headers headers = t.getRequestHeaders();
        String contentType;
        if (headers.containsKey("Content-Type")) {
            contentType = headers.get("Content-Type").get(0);

            if (contentType.equals("application/json")) {
                InputStream stream = t.getRequestBody();
                
                String jsonString = new BufferedReader(new InputStreamReader(stream,
                    StandardCharsets.UTF_8)).lines().collect(Collectors.joining("\n"));

                stream.close();

                try {
                    JSONObject commentJSON = new JSONObject(jsonString);

                    int coordinateId = commentJSON.getInt("id");
                    String comment = commentJSON.getString("comment");
                    String timestampString = commentJSON.getString("sent");
                    
                    if (coordinateId <= 0 || comment.length() == 0 ||
                            timestampString.length() == 0) {
                        String response = "Coordinate info not proper\n";

                        sendResponse(response, 400, t);
                    } else {
                        db.setComment(
                            new CoordinateComment(coordinateId, comment, timestampString));
                        
                        sendResponse("", 200, t);
                    }
                } catch (JSONException | DateTimeParseException | SQLException e) {
                    String response = "Error when parsing JSON object\n";

                    sendResponse(response, 400, t);
                }
            } else {
                String response = "Wrong content type. Only JSON is supported\n";

                sendResponse(response, 400, t);
            }
        } else {
            String response = "No content type available\n";

            sendResponse(response, 400, t);
        }
    }

    private void sendResponse(String response, int responseCode, HttpExchange t) throws IOException {
        if (!response.isBlank()) {
            byte [] bytes = response.getBytes(StandardCharsets.UTF_8);
            t.sendResponseHeaders(responseCode, bytes.length);
            OutputStream messageBodyStream = t.getResponseBody();
            messageBodyStream.write(bytes);
            messageBodyStream.close();
        } else {
            t.sendResponseHeaders(responseCode, -1);
            t.close();
        }
    }
}
