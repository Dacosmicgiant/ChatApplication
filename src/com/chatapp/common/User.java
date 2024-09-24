// This file defines the User class which represents a user in the chat application
// src/com/chatapp/common/User.java

package com.chatapp.common;

public class User {

    // Fields for storing username and password
    // Note: In production, passwords should be hashed for security purposes.
    private String username;
    private String password; // Plaintext storage should be avoided in production

    // Default constructor - allows creation of a User object without passing any
    // initial values
    public User() {
    }

    // Parameterized constructor - allows initialization of a User object with
    // specific username and password
    public User(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getter for username - returns the current username
    public String getUsername() {
        return username;
    }

    // Setter for username - allows updating the username
    public void setUsername(String username) {
        this.username = username;
    }

    // Getter for password - returns the current password
    // Note: Exposing raw passwords like this is not recommended in production
    public String getPassword() {
        return password;
    }

    // Setter for password - allows updating the password
    // In production, consider storing a hashed version of the password instead of
    // plaintext
    public void setPassword(String password) {
        this.password = password;
    }
}
