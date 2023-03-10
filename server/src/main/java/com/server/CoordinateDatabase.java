package com.server;

import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

public class CoordinateDatabase {
    private Connection dbConnection = null;
    private static CoordinateDatabase dbInstance = null;

    public static synchronized CoordinateDatabase getInstance() {
        if (dbInstance == null) {
            dbInstance = new CoordinateDatabase();
        }
        return dbInstance;
    }
 
    public void open(String dbName) throws SQLException, IOException {
        File dbFile = new File(".", dbName);
        boolean foundDB = (dbFile.exists() && !dbFile.isDirectory());
        if (!foundDB) {
            dbFile.createNewFile();
        }
        String databaseURL = "jdbc:sqlite:" + dbFile.getAbsolutePath();
        dbConnection = DriverManager.getConnection(databaseURL);
        if (!foundDB) {
            initializeDatabase();
        }

    }

    private boolean initializeDatabase() throws SQLException {
        if (dbConnection != null) {
            String createUsersString = "CREATE TABLE users(" + 
                "username VARCHAR(20) PRIMARY KEY," +
                "password VARCHAR(200) NOT NULL," +
                "salt VARCHAR(100) NOT NULL," +
                "email VARCHAR(50) NOT NULL)";
            
            String createCoordinatesString = "CREATE TABLE coordinates(" +
                "id INTEGER PRIMARY KEY," +
                "nick VARCHAR(20) NOT NULL," +
                "latitude REAL NOT NULL," +
                "longitude REAL NOT NULL," +
                "sent DATE NOT NULL," + 
                "description VARCHAR(1024))";
            
            String createCommentsString = "CREATE TABLE comments(" +
                "coordinate_id INTEGER," +
                "comment VARCHAR(1024)," +
                "sent DATE NOT NULL," +
                "FOREIGN KEY(coordinate_id) REFERENCES coordinates(id))";
            
            try (Statement createStatement = dbConnection.createStatement()) {
                createStatement.execute(createUsersString);
                createStatement.execute(createCoordinatesString);
                createStatement.execute(createCommentsString);
                return true;
            }
        }
        return false;
    }

    public void setUser(User user) throws SQLException {
        String setUserString = "INSERT INTO users VALUES('"+
            user.getUsername() + "','" +
            user.getPassword() + "','" +
            user.getSalt() + "','" +
            user.getEmail() + "')";
        
        try (Statement createStatement = dbConnection.createStatement()) {
            createStatement.executeUpdate(setUserString);
        }
    }

    public void setCoordinate(UserCoordinate coordinate) throws SQLException {
        String setCoordinateString = "INSERT INTO coordinates" +
            "(nick, latitude, longitude, sent, description) VALUES('"+
            coordinate.getNick() + "','" +
            coordinate.getLatitude() + "','" +
            coordinate.getLongitude() + "'," +
            coordinate.getTimestampAsLong() + ",'" +
            coordinate.getDescription() + "')";
        
        try (Statement createStatement = dbConnection.createStatement()) {
            createStatement.executeUpdate(setCoordinateString);
        }
    }

    public void setComment(CoordinateComment comment) throws SQLException {
        String setCommentString = "INSERT INTO comments VALUES("+
            comment.getCoordinateId() + ",'" +
            comment.getCommentBody() + "'," +
            comment.getTimestampAsLong() + ")";
        
        try (Statement createStatement = dbConnection.createStatement()) {
            createStatement.executeUpdate(setCommentString);
        }
    }

    public User getUserByUsername(String username) throws SQLException {
        String getUserString = "SELECT * FROM users WHERE username = '" + username + "'";
        
        try (Statement createStatement = dbConnection.createStatement()) {
            ResultSet resultsSet = createStatement.executeQuery(getUserString);
            User user = new User();
            user.setUsername(resultsSet.getString("username"));
            user.setPassword(resultsSet.getString("password"));
            user.setSalt(resultsSet.getString("salt"));
            user.setEmail(resultsSet.getString("email"));
            return user;
        }
    }

    public ArrayList<UserCoordinate> getCoordinates() throws SQLException {
        String getCoordinatesString = "SELECT * FROM coordinates";
        
        try (Statement createStatement = dbConnection.createStatement())  {
            ResultSet resultSet = createStatement.executeQuery(getCoordinatesString);
            ArrayList<UserCoordinate> coordinates = new ArrayList<>();
            while (resultSet.next()) {
                UserCoordinate coordinate = new UserCoordinate();
                coordinate.setId(resultSet.getInt("id"));
                coordinate.setNick(resultSet.getString("nick"));
                coordinate.setLatitude(resultSet.getDouble("latitude"));
                coordinate.setLongitude(resultSet.getDouble("longitude"));
                coordinate.setTimestamp(resultSet.getLong("sent"));
                coordinate.setDescription(resultSet.getString("description"));
                coordinates.add(coordinate);
            }
            return coordinates;
        }
    }

    public ArrayList<CoordinateComment> getCoordinateComments(UserCoordinate coordinate)
            throws SQLException {
        String getCommentsString = "SELECT comment, sent " + 
            "FROM comments WHERE coordinate_id =" + coordinate.getId();
        
        try (Statement createStatement = dbConnection.createStatement()) {
            ResultSet resultSet = createStatement.executeQuery(getCommentsString);
            ArrayList<CoordinateComment> comments = new ArrayList<>();
            while (resultSet.next()) {
                CoordinateComment comment = new CoordinateComment();
                comment.setCommentBody(resultSet.getString("comment"));
                comment.setTimestamp(resultSet.getLong("sent"));
                comments.add(comment);
            }
            return comments;
        }
    }

    public ArrayList<UserCoordinate> getCoordinatesByNickname(String nickname) throws SQLException {
        String getCoordinatesString = "SELECT * FROM coordinates " + 
            "WHERE nick = '" + nickname + "'";
        
        try (Statement createStatement = dbConnection.createStatement()) {
            ResultSet resultSet = createStatement.executeQuery(getCoordinatesString);
            ArrayList<UserCoordinate> coordinates = new ArrayList<>();
            while (resultSet.next()) {
                UserCoordinate coordinate = new UserCoordinate();
                coordinate.setId(resultSet.getInt("id"));
                coordinate.setNick(resultSet.getString("nick"));
                coordinate.setLatitude(resultSet.getDouble("latitude"));
                coordinate.setLongitude(resultSet.getDouble("longitude"));
                coordinate.setTimestamp(resultSet.getLong("sent"));
                coordinate.setDescription(resultSet.getString("description"));
                coordinates.add(coordinate);
            }
            return coordinates;
        }
    }

    public ArrayList<UserCoordinate> getCoordinatesByTime(long timeStart, long timeEnd)
            throws SQLException {
        String getCoordinatesString = "SELECT * FROM coordinates " + 
            "WHERE sent >= " + timeStart + " AND  sent <= " + timeEnd;
        
        try (Statement createStatement = dbConnection.createStatement()) {
            ResultSet resultSet = createStatement.executeQuery(getCoordinatesString);
            ArrayList<UserCoordinate> coordinates = new ArrayList<>();
            while (resultSet.next()) {
                UserCoordinate coordinate = new UserCoordinate();
                coordinate.setId(resultSet.getInt("id"));
                coordinate.setNick(resultSet.getString("nick"));
                coordinate.setLatitude(resultSet.getDouble("latitude"));
                coordinate.setLongitude(resultSet.getDouble("longitude"));
                coordinate.setTimestamp(resultSet.getLong("sent"));
                coordinate.setDescription(resultSet.getString("description"));
                coordinates.add(coordinate);
            }
            return coordinates;
        }
    }

    public void close() throws SQLException{
        dbConnection.close();
    }
}
