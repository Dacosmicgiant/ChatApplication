// src/com/chatapp/server/ClientHandler.java
package com.chatapp.server;

import com.chatapp.common.Message;
import com.chatapp.common.User;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable {
    private Socket socket;
    private ChatServer server;
    private AuthenticationService authService;
    private BufferedReader in;
    private PrintWriter out;
    private String username;
    private boolean authenticated;
    private Gson gson;

    public ClientHandler(Socket socket, ChatServer server, AuthenticationService authService) {
        this.socket = socket;
        this.server = server;
        this.authService = authService;
        this.authenticated = false;
        this.gson = new Gson();
        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(socket.getOutputStream(), true);
        } catch (IOException e) {
            System.err.println("Error initializing ClientHandler: " + e.getMessage());
        }
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getUsername() {
        return username;
    }

    public void sendMessage(String message) {
        out.println(message);
    }

    @Override
    public void run() {
        try {
            // Handle authentication
            while (!authenticated) {
                String authMessage = in.readLine();
                if (authMessage == null) {
                    break;
                }

                Message message = gson.fromJson(authMessage, Message.class);
                if ("auth".equals(message.getType())) {
                    String user = message.getSender();
                    String pass = message.getContent();

                    if (authService.authenticate(user, pass)) {
                        authenticated = true;
                        username = user;
                        out.println(gson.toJson(new Message("auth_success", "Server", "Authentication successful")));
                        server.broadcast(gson.toJson(new Message("system", "Server", user + " has joined the chat.")),
                                this);
                    } else {
                        out.println(gson.toJson(new Message("auth_failure", "Server", "Invalid credentials")));
                    }
                }
            }

            // Handle incoming messages
            String clientMessage;
            while ((clientMessage = in.readLine()) != null) {
                Message message = gson.fromJson(clientMessage, Message.class);
                if ("message".equals(message.getType())) {
                    System.out.println(username + ": " + message.getContent());
                    server.broadcast(gson.toJson(message), this);
                }
                // Handle other message types as needed
            }
        } catch (IOException e) {
            System.err.println("Error in ClientHandler: " + e.getMessage());
        } finally {
            try {
                server.removeClient(this);
                server.broadcast(gson.toJson(new Message("system", "Server", username + " has left the chat.")), this);
                socket.close();
            } catch (IOException e) {
                System.err.println("Error closing socket: " + e.getMessage());
            }
        }
    }
}
