package com.server;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.*;

public class UserAuthenticator extends BasicAuthenticator{
    
    public UserAuthenticator() {
        super("coordinates");
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        /** Gets user password if user exists and then verifies the password.
        Returns true for valid user/password, false otherwise */
        CoordinateDatabase db = CoordinateDatabase.getInstance();
        
        try {
            User user = db.getUserByUsername(username);
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        } catch (SQLException e) {
            return false;
        }
        return false;
    }

    public boolean addUser(String username, String password, String email){
        CoordinateDatabase db = CoordinateDatabase.getInstance();

        try {
            User user = db.getUserByUsername(username);
            if (user.getUsername().equals(username)) {
                return false;
            }
        } catch (SQLException e) {
            //
        }

        try {
            db.setUser(new User(username, password, email));
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
}
