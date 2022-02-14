package com.server;

import java.util.Hashtable;
import java.util.Map;

import com.sun.net.httpserver.*;

public class UserAuthenticator extends BasicAuthenticator{
    private Map<String, String> users = null;

    public UserAuthenticator() {
        super("coordinates");
        users = new Hashtable<>();

        users.put("dummy", "passwd");
    }

    public boolean checkCredentials(String username, String password) {
        /** Gets user password if user exists and then verifies the password.
        Returns true for valid user/password, false otherwise */
        return password.equals(users.get(username));
    }
}
