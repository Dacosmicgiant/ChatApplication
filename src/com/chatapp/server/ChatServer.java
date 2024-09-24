package com.chatapp.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class ChatServer {
    // The port number on which the chat server will listen for incoming connections
    private static final int PORT = 12345;

    // List to store all the connected clients (client handlers)
    private List<ClientHandler> clients;

    // Instance of the authentication service to authenticate users
    private AuthenticationService authService;

    // Constructor to initialize the list of clients and the authentication service
    public ChatServer() {
        clients = new ArrayList<>();
        authService = new AuthenticationService();
    }

    // Method to start the chat server
    public void start() {
        // Create a server socket to listen for client connections
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Chat server started on port " + PORT);

            // Continuously listen for incoming client connections
            while (true) {
                // Accept a new client connection
                Socket socket = serverSocket.accept();
                System.out.println("New client connected: " + socket.getInetAddress());

                // Create a new ClientHandler for the connected client and start a new thread
                // for it
                ClientHandler clientHandler = new ClientHandler(socket, this, authService);
                clients.add(clientHandler);
                new Thread(clientHandler).start(); // Handle client interaction in a separate thread
            }
        } catch (IOException e) {
            // Print an error message if the server fails to start
            System.err.println("Error starting server: " + e.getMessage());
        }
    }

    // Method to send a message to all clients except the sender
    public void broadcast(String message, ClientHandler sender) {
        for (ClientHandler client : clients) {
            // Only send the message to clients who are authenticated and not the sender
            if (client != sender && client.isAuthenticated()) {
                client.sendMessage(message);
            }
        }
    }

    // Method to remove a client from the list when they disconnect
    public void removeClient(ClientHandler clientHandler) {
        clients.remove(clientHandler);
        System.out.println("Client disconnected: " + clientHandler.getUsername());
    }

    // Main method to start the chat server
    public static void main(String[] args) {
        new ChatServer().start(); // Create a ChatServer instance and start it
    }
}
