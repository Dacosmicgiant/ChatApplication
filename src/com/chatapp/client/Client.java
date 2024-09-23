// src/com/chatapp/client/Client.java
package com.chatapp.client;

import com.chatapp.common.Message;
import com.google.gson.Gson;

import java.io.*;
import java.net.Socket;

public class Client {
    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Gson gson;

    public Client(String host, int port) throws IOException {
        gson = new Gson();
        socket = new Socket(host, port);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    public void sendMessage(Message message) {
        out.println(gson.toJson(message));
    }

    public String receiveMessage() throws IOException {
        return in.readLine();
    }

    public void close() throws IOException {
        socket.close();
    }
}
