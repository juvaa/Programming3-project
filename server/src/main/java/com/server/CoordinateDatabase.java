package com.server;


import java.io.File;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

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
                "password VARCHAR(100) NOT NULL,"+
                "email VARCHAR(50) NOT NULL)";
            String createCoordinatesString = "CREATE TABLE coordinates("+
                "nick VARCHAR(20),"+
                "latitude VARCHAR(20),"+
                "longitude VARCHAR(20),"+
                "sent DATE,"+
                "PRIMARY KEY (nick, latitude, longitude, sent))";
            Statement createStatement = dbConnection.createStatement();
            createStatement.execute(createUsersString);
            createStatement.execute(createCoordinatesString);
            createStatement.close();
            return true;
        }
        return false;
    }
}
