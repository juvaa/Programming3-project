package com.server;

import java.security.SecureRandom;
import java.sql.SQLException;
import java.util.Base64;

import com.sun.net.httpserver.BasicAuthenticator;

import org.apache.commons.codec.digest.Crypt;

public class UserAuthenticator extends BasicAuthenticator{
    private final SecureRandom secureRandom = new SecureRandom();
    
    public UserAuthenticator() {
        super("coordinates");
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        /* Gets user password if user exists and then verifies the password.
        Returns true for valid user/password, false otherwise */
        CoordinateDatabase db = CoordinateDatabase.getInstance();
        
        try {
            User user = db.getUserByUsername(username);
            String hashedPassword = user.getPassword();
            if (hashedPassword.equals(Crypt.crypt(password, hashedPassword))) {
                return true;
            }
        } catch (SQLException e) {
            // No user found with the specified username
        }
        return false;
    }

    public boolean addUser(String username, String password, String email){
        CoordinateDatabase db = CoordinateDatabase.getInstance();

        try {
            db.getUserByUsername(username);
            // If user is found
            return false;
        } catch (SQLException e) {
            // No user found with the specified username, moving forward
        }

        byte[] bytes = new byte[13];
        secureRandom.nextBytes(bytes);
        String saltBytes = new String(Base64.getEncoder().encode(bytes));
        String salt = "$6$" + saltBytes;

        String hashedPassword = Crypt.crypt(password, salt);

        try {
            db.setUser(new User(username, hashedPassword, salt, email));
        } catch (SQLException e) {
            return false;
        }
        return true;
    }
}
