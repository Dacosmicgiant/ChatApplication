// src/com/chatapp/server/AuthenticationService.java
package com.chatapp.server;

import java.util.HashMap;
import java.util.Map;

/**
 * This class handles user authentication and registration for the chat
 * application.
 * It stores user credentials in memory and provides methods for user login and
 * registration.
 */
public class AuthenticationService {
    // A map to store user credentials (username:password pairs)
    private Map<String, String> userCredentials;

    /**
     * Constructor initializes the user credentials map with predefined users.
     * You can add more users to this map manually or through the register method.
     */
    public AuthenticationService() {
        userCredentials = new HashMap<>();
        // Predefined users (username:password)
        userCredentials.put("user1", "password1");
        userCredentials.put("user2", "password2");
        // Add more users as needed
    }

    /**
     * Authenticates the user by checking if the provided username exists and
     * if the corresponding password matches the stored password.
     * 
     * @param username the username of the user trying to authenticate
     * @param password the password of the user trying to authenticate
     * @return true if authentication is successful, false otherwise
     */
    public boolean authenticate(String username, String password) {
        if (userCredentials.containsKey(username)) {
            // Check if the password matches the stored password for this username
            return userCredentials.get(username).equals(password);
        }
        return false; // Username not found
    }

    /**
     * Registers a new user with a username and password.
     * 
     * @param username the username of the new user
     * @param password the password of the new user
     * @return true if registration is successful, false if the username already
     *         exists
     */
    public boolean register(String username, String password) {
        if (userCredentials.containsKey(username)) {
            return false; // User already exists, cannot register again
        }
        // Add new user to the credentials map
        userCredentials.put(username, password);
        return true; // Registration successful
    }
}
