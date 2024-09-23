// src/com/chatapp/server/AuthenticationService.java
package com.chatapp.server;

import java.util.HashMap;
import java.util.Map;

public class AuthenticationService {
    private Map<String, String> userCredentials;

    public AuthenticationService() {
        userCredentials = new HashMap<>();
        // Predefined users (username:password)
        userCredentials.put("user1", "password1");
        userCredentials.put("user2", "password2");
        // Add more users as needed
    }

    public boolean authenticate(String username, String password) {
        if (userCredentials.containsKey(username)) {
            return userCredentials.get(username).equals(password);
        }
        return false;
    }

    public boolean register(String username, String password) {
        if (userCredentials.containsKey(username)) {
            return false; // User already exists
        }
        userCredentials.put(username, password);
        return true;
    }
}
