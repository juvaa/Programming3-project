package com.server;

import com.sun.net.httpserver.*;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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

public class CoordinatesHandler implements HttpHandler {

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
                        JSONObject coordinateJSON = new JSONObject(jsonString);
                        String nick = coordinateJSON.getString("username");
                        String longitude = coordinateJSON.getString("longitude");
                        String latitude = coordinateJSON.getString("latitude");
                        String timestampString = coordinateJSON.getString("sent");

                        if (nick.length() == 0 || longitude.length() == 0 || 
                                latitude.length() == 0 || timestampString.length() == 0) {
                            String reponse = "Coordinate info not proper\n";
    
                            byte [] bytes = reponse.getBytes(StandardCharsets.UTF_8);
                            t.sendResponseHeaders(400, bytes.length);
                            OutputStream messageBodyStream = t.getResponseBody();
                            messageBodyStream.write(bytes);
                            messageBodyStream.close();
                        }
                        else {
                            db.setCoordinate(new UserCoordinate(nick, latitude, longitude, timestampString));
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
        } else if (t.getRequestMethod().equalsIgnoreCase("GET")) {
            ArrayList<UserCoordinate> coordinates = new ArrayList<>();
            try {
                coordinates = db.getCoordinates();
            } catch (SQLException e) {
                t.sendResponseHeaders(204, -1);
            }
            if (coordinates.isEmpty()) {
                t.sendResponseHeaders(204, -1);
            } else {
                JSONArray reponseCoordinates = new JSONArray();
                for (UserCoordinate coordinate : coordinates) {
                    JSONObject jsonCoordinate = new JSONObject();
                    jsonCoordinate
                        .put("username", coordinate.getNick())
                        .put("latitude", coordinate.getLatitude())
                        .put("longitude", coordinate.getLongitude())
                        .put("sent", coordinate.getTimestampString());
                    reponseCoordinates.put(jsonCoordinate);
                }
                String response = reponseCoordinates.toString();
                byte [] bytes = response.getBytes(StandardCharsets.UTF_8);
                t.sendResponseHeaders(200, bytes.length);
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
