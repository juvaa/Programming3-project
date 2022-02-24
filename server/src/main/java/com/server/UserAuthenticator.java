package com.server;

import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.*;

public class UserAuthenticator extends BasicAuthenticator{
    private List<User> users = null;

    public UserAuthenticator() {
        super("coordinates");
        users = new ArrayList<>();
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        /** Gets user password if user exists and then verifies the password.
        Returns true for valid user/password, false otherwise */
        for (User user : users) {
            if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
                return true;
            }
        }
        return false;
    }

    public boolean addUser(String username, String password, String email) {
        for (User user: users) {
            if (user.getUsername().equals(username)) {
                return false;
            }
        }
        users.add(new User(username, password, email));
        return true;
    }
}
