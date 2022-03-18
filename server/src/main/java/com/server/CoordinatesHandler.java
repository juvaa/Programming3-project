package com.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.SQLException;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.stream.Collectors;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class CoordinatesHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange t) throws IOException {
        if (t.getRequestMethod().equalsIgnoreCase("POST")) {
            handlePost(t);
        } else if (t.getRequestMethod().equalsIgnoreCase("GET")) {
            handleGet(t);
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
                    JSONObject coordinateJSON = new JSONObject(jsonString);
                    String nick = coordinateJSON.getString("username");
                    double longitude = coordinateJSON.getDouble("longitude");
                    double latitude = coordinateJSON.getDouble("latitude");
                    String timestampString = coordinateJSON.getString("sent");
                    String description;
                    if (coordinateJSON.has("description")) {
                        description = coordinateJSON.getString("description");
                    } else description = "nodata";

                    if (nick.length() == 0 || longitude == 0 || 
                            latitude == 0 || timestampString.length() == 0) {
                        String response = "Coordinate info not proper\n";

                        sendResponse(response, 400, t);
                    } else {
                        db.setCoordinate(
                            new UserCoordinate(
                                nick, latitude, longitude, timestampString, description));
                        
                        // Coordinate received succesfully
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

    /* TODO: Maybe refactor the if-else try-catch spaghetti */
    private void handleGet(HttpExchange t) throws IOException {
        CoordinateDatabase db = CoordinateDatabase.getInstance();
        ArrayList<UserCoordinate> coordinates = new ArrayList<>();
        try {
            coordinates = db.getCoordinates();
        } catch (SQLException e) {
            sendResponse("", 204, t);
        }
        if (coordinates.isEmpty()) {
            sendResponse("", 204, t);
        } else {
            JSONArray reponseCoordinates = new JSONArray();
            for (UserCoordinate coordinate : coordinates) {
                ArrayList<CoordinateComment> comments = new ArrayList<>();
                JSONObject jsonCoordinate = new JSONObject();
                jsonCoordinate
                    .put("id", coordinate.getId())
                    .put("username", coordinate.getNick())
                    .put("latitude", coordinate.getLatitude())
                    .put("longitude", coordinate.getLongitude())
                    .put("sent", coordinate.getTimestampString());
                
                if (!coordinate.getDescription().equals("nodata"))
                    jsonCoordinate.put("description", coordinate.getDescription());

                try {
                    comments = db.getCoordinateComments(coordinate);
                } catch (SQLException e) {
                    //
                }

                if (!comments.isEmpty()) {
                    JSONArray commentsArray = new JSONArray();
                    for (CoordinateComment comment: comments) {
                        JSONObject jsonComment = new JSONObject();
                        jsonComment
                            .put("comment", comment.getCommentBody())
                            .put("sent", comment.getTimestampString());
                        commentsArray.put(jsonComment);
                    }
                    jsonCoordinate.put("comments", commentsArray);
                }
                reponseCoordinates.put(jsonCoordinate);
            }
            String response = reponseCoordinates.toString();
            byte [] bytes = response.getBytes(StandardCharsets.UTF_8);
            t.getResponseHeaders().add("Content-Type", "application/json");
            t.sendResponseHeaders(200, bytes.length);
            OutputStream messageBodyStream = t.getResponseBody();
            messageBodyStream.write(bytes);
            messageBodyStream.close();
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
