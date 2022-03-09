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
                "username VARCHAR(20) PRIMARY KEY,"+
                "password VARCHAR(200) NOT NULL,"+
                "salt VARCHAR(100) NOT NULL,"+
                "email VARCHAR(50) NOT NULL)";
            String createCoordinatesString = "CREATE TABLE coordinates("+
                "nick VARCHAR(20) NOT NULL,"+
                "latitude REAL NOT NULL,"+
                "longitude REAL NOT NULL,"+
                "sent DATE NOT NULL)";
            Statement createStatement = dbConnection.createStatement();
            createStatement.execute(createUsersString);
            createStatement.execute(createCoordinatesString);
            createStatement.close();
            return true;
        }
        return false;
    }

    public void setUser(User user) throws SQLException {
        String setUserString = "INSERT INTO users VALUES('"+
            user.getUsername() + "','" +
            user.getPassword() + "','" +
            user.getSalt() + "','" +
            user.getEmail() + "')";
        Statement createStatement = dbConnection.createStatement();
        createStatement.executeUpdate(setUserString);
        createStatement.close();
    }

    public void setCoordinate(UserCoordinate coordinate) throws SQLException {
        String setCoordinateString = "INSERT INTO coordinates VALUES('"+
            coordinate.getNick() + "','" +
            coordinate.getLatitude() + "','" +
            coordinate.getLongitude() + "'," +
            coordinate.getTimestampAsLong() + ")";
        Statement createStatement = dbConnection.createStatement();
        createStatement.executeUpdate(setCoordinateString);
        createStatement.close();
    }

    public User getUserByUsername(String username) throws SQLException {
        String getUserString = "SELECT * FROM users WHERE username = '" + username + "'";
        Statement creaStatement = dbConnection.createStatement();
        ResultSet resultsSet = creaStatement.executeQuery(getUserString);
        User user = new User();
        user.setUsername(resultsSet.getString(1));
        user.setPassword(resultsSet.getString(2));
        user.setEmail(resultsSet.getString(3));
        creaStatement.close();
        return user;
    }

    public ArrayList<UserCoordinate> getCoordinates() throws SQLException {
        String getCoordinatesString = "SELECT * FROM coordinates";
        Statement creaStatement = dbConnection.createStatement();
        ResultSet resultSet = creaStatement.executeQuery(getCoordinatesString);
        ArrayList<UserCoordinate> coordinates = new ArrayList<>();
        while (resultSet.next()) {
            UserCoordinate coordinate = new UserCoordinate();
            coordinate.setNick(resultSet.getString(1));
            coordinate.setLatitude(resultSet.getDouble(2));
            coordinate.setLongitude(resultSet.getDouble(3));
            coordinate.setTimestamp(resultSet.getLong(4));
            coordinates.add(coordinate);
        }
        return coordinates;
    }
}
