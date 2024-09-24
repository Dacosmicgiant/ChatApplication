// src/com/chatapp/client/Client.java

// Package declaration for client-side functionality of the chat application
package com.chatapp.client;

import com.chatapp.common.Message; // Importing the Message class for message handling
import com.google.gson.Gson; // Importing Gson library for JSON conversion

import java.io.*; // Importing I/O classes for reading and writing data
import java.net.Socket; // Importing Socket class to handle network communication

// The Client class is responsible for managing the client's connection to the server.
public class Client {
    private Socket socket; // Socket for establishing connection with the server
    private BufferedReader in; // Reader to receive messages from the server
    private PrintWriter out; // Writer to send messages to the server
    private Gson gson; // Gson instance for converting Message objects to JSON format

    // Constructor to initialize the client by connecting to the server
    public Client(String host, int port) throws IOException {
        gson = new Gson(); // Instantiate Gson object for JSON serialization
        socket = new Socket(host, port); // Create a new socket connection to the server at the specified host and port
        in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Initialize the input stream to read
                                                                                 // server responses
        out = new PrintWriter(socket.getOutputStream(), true); // Initialize the output stream to send data to the
                                                               // server
    }

    // Method to send a message to the server
    public void sendMessage(Message message) {
        // Convert the Message object to JSON and send it to the server
        out.println(gson.toJson(message));
    }

    // Method to receive a message from the server
    public String receiveMessage() throws IOException {
        // Read a line of input from the server (blocking call)
        return in.readLine();
    }

    // Method to close the client connection
    public void close() throws IOException {
        // Close the socket and all associated streams
        socket.close();
    }
}
