// src/com/chatapp/server/ClientHandler.java
package com.chatapp.server;

import com.chatapp.common.Message;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

/**
 * ClientHandler class manages the connection between the server and an
 * individual client.
 * It handles authentication, incoming messages, and sending responses back to
 * the client.
 */
public class ClientHandler implements Runnable {
    private Socket socket; // Socket to communicate with the client
    private ChatServer server; // Reference to the main chat server
    private AuthenticationService authService; // Service to authenticate users
    private BufferedReader in; // Input stream to read messages from the client
    private PrintWriter out; // Output stream to send messages to the client
    private String username; // The username of the connected client
    private boolean authenticated; // Flag indicating whether the client is authenticated
    private Gson gson; // Gson library for converting messages to/from JSON format

    /**
     * Constructor initializes the ClientHandler with a socket, server, and
     * authentication service.
     * It also sets up the input and output streams for communication with the
     * client.
     *
     * @param socket      The socket representing the client's connection
     * @param server      The ChatServer instance managing all clients
     * @param authService The AuthenticationService to validate user credentials
     */
    public ClientHandler(Socket socket, ChatServer server, AuthenticationService authService) {
        this.socket = socket;
        this.server = server;
        this.authService = authService;
        this.authenticated = false; // Client starts as unauthenticated
        this.gson = new Gson(); // Initialize Gson for JSON handling
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Error initializing ClientHandler: " + e.getMessage());
            e.printStackTrace(); // Debug information in case of error
        }
    }

    /**
     * Returns whether the client has been authenticated.
     * 
     * @return true if authenticated, false otherwise.
     */
    public boolean isAuthenticated() {
        return authenticated;
    }

    /**
     * Returns the username of the connected client.
     * 
     * @return The username of the client.
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sends a message to the client.
     * 
     * @param message The message to send.
     */
    public void sendMessage(String message) {
        out.println(message); // Sends the message through the output stream
    }

    /**
     * The main execution of the ClientHandler. This handles the authentication
     * process,
     * listens for client messages, and responds accordingly.
     */
    @Override
    public void run() {
        try {
            // Handle the authentication process
            while (!authenticated) {
                String authMessage = in.readLine(); // Read the client's authentication message
                System.out.println("Received authentication message: " + authMessage); // Debug

                if (authMessage == null) {
                    break; // Exit loop if no message is received (client disconnects)
                }

                Message message = gson.fromJson(authMessage, Message.class); // Parse the message as JSON

                // Check if the message is for authentication
                if ("auth".equals(message.getType())) {
                    String user = message.getSender(); // Extract username
                    String pass = message.getContent(); // Extract password

                    System.out.println("Authenticating user: " + user); // Debug

                    // Authenticate the user using the authentication service
                    if (authService.authenticate(user, pass)) {
                        authenticated = true; // Set flag if authentication is successful
                        username = user; // Store the username
                        out.println(gson.toJson(new Message("auth_success", "Server", "Authentication successful"))); // Inform
                                                                                                                      // the
                                                                                                                      // client
                        server.broadcast(gson.toJson(new Message("system", "Server", user + " has joined the chat.")),
                                this); // Notify other clients
                        System.out.println("User " + user + " authenticated successfully."); // Debug
                    } else {
                        // If authentication fails, inform the client
                        out.println(gson.toJson(new Message("auth_failure", "Server", "Invalid credentials")));
                        System.out.println("Authentication failed for user: " + user); // Debug
                    }
                }
            }

            // Handle incoming messages from the client
            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                Message message = gson.fromJson(clientMessage, Message.class); // Parse the client's message

                // Check if the message is a regular chat message
                if ("message".equals(message.getType())) {
                    System.out.println(username + ": " + message.getContent()); // Debug
                    server.broadcast(gson.toJson(message), this); // Broadcast the message to other clients
                }
                // Additional message types can be handled here (e.g., private messages, system
                // commands)
            }
        } catch (IOException e) {
            System.err.println("Error in ClientHandler: " + e.getMessage());
            e.printStackTrace(); // Debug information in case of an error
        } finally {
            // Clean up and handle client disconnection
            try {
                server.removeClient(this); // Remove the client from the server's client list
                server.broadcast(gson.toJson(new Message("system", "Server", username + " has left the chat.")), this); // Notify
                                                                                                                        // other
                                                                                                                        // clients
                socket.close(); // Close the socket connection
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
                e.printStackTrace(); // Debug information in case of error
            }
        }
    }
}
