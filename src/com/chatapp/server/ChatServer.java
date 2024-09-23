package com.chatapp.server;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class ChatServer {
    private static final int DEFAULT_PORT = 12345;
    private ServerSocket serverSocket;
    private AuthenticationService authService;

    public ChatServer(int port) throws IOException {
        serverSocket = new ServerSocket(port, 0, InetAddress.getByName("0.0.0.0"));
        authService = new AuthenticationService();
        System.out.println("Chat server started on port " + port);
    }

    public void start() {
        try {
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New client connected: " + clientSocket.getInetAddress());
                ClientHandler handler = new ClientHandler(clientSocket, this, authService);
                new Thread(handler).start();
            }
        } catch (IOException e) {
            System.err.println("Error accepting client connection: " + e.getMessage());
        } finally {
            stop();
        }
    }

    public void stop() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            System.err.println("Error closing server: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        int port;
        try {
            String portEnv = System.getenv("PORT");
            port = portEnv != null ? Integer.parseInt(portEnv) : DEFAULT_PORT;
        } catch (NumberFormatException e) {
            port = DEFAULT_PORT;
        }

        try {
            ChatServer server = new ChatServer(port);
            server.start();
        } catch (IOException e) {
            System.err.println("Failed to start server: " + e.getMessage());
        }
    }
}
