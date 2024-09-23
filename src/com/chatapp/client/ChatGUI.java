// src/com/chatapp/client/ChatGUI.java
package com.chatapp.client;

import javafx.application.Platform;
import com.chatapp.common.Message;
import com.google.gson.Gson;
import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;

public class ChatGUI extends Application {
    private TextArea chatArea;
    private TextField inputField;
    private Button sendButton;

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Gson gson;

    private String username;

    @Override
    public void start(Stage primaryStage) {
        gson = new Gson();

        // Login UI
        Stage loginStage = new Stage();
        loginStage.setTitle("Login");

        Label userLabel = new Label("Username:");
        TextField userField = new TextField();
        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();
        Button loginButton = new Button("Login");

        VBox loginLayout = new VBox(10, userLabel, userField, passLabel, passField, loginButton);
        loginLayout.setPadding(new Insets(20));
        loginStage.setScene(new Scene(loginLayout, 300, 200));
        loginStage.show();

        loginButton.setOnAction(e -> {
            String user = userField.getText().trim();
            String pass = passField.getText().trim();
            if (!user.isEmpty() && !pass.isEmpty()) {
                try {
                    connectToServer();
                    authenticate(user, pass);
                } catch (IOException ex) {
                    showAlert("Connection Error", "Unable to connect to server.");
                }
            } else {
                showAlert("Input Error", "Please enter username and password.");
            }
        });

        // Main Chat UI
        chatArea = new TextArea();
        chatArea.setEditable(false);
        inputField = new TextField();
        sendButton = new Button("Send");

        HBox inputLayout = new HBox(10, inputField, sendButton);
        inputLayout.setPadding(new Insets(10));

        VBox mainLayout = new VBox(10, chatArea, inputLayout);
        Scene mainScene = new Scene(mainLayout, 600, 400);

        sendButton.setOnAction(e -> sendMessage());
        inputField.setOnAction(e -> sendMessage());

        loginStage.setOnCloseRequest(e -> {
            try {
                if (socket != null && !socket.isClosed()) {
                    socket.close();
                }
            } catch (IOException ex) {
                // Ignore
            }
        });

        // Switch to main chat UI after successful login
        primaryStage.setOnShown(e -> {
            // Will show main chat UI after login
        });

        // Keep primaryStage hidden initially
        primaryStage.setScene(new Scene(new Pane(), 0, 0));
    }

    private void connectToServer() throws IOException {
        socket = new Socket("localhost", 12345);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);

        // Start a listener thread
        new Thread(this::listenForMessages).start();
    }

    private void authenticate(String user, String pass) {
        Message authMessage = new Message("auth", user, pass);
        out.println(gson.toJson(authMessage));

        try {
            String response = in.readLine();
            Message resp = gson.fromJson(response, Message.class);
            if ("auth_success".equals(resp.getType())) {
                username = user;
                Platform.runLater(() -> {
                    Stage chatStage = new Stage();
                    chatStage.setTitle("Chat - " + username);
                    chatStage.setScene(new Scene(chatArea.getScene().getRoot()));
                    chatStage.show();
                });
            } else {
                showAlert("Authentication Failed", resp.getContent());
                socket.close();
            }
        } catch (IOException e) {
            showAlert("Authentication Error", "Failed to authenticate.");
        }
    }

    private void listenForMessages() {
        String message;
        try {
            while ((message = in.readLine()) != null) {
                Message msg = gson.fromJson(message, Message.class);
                if ("message".equals(msg.getType()) || "system".equals(msg.getType())) {
                    Platform.runLater(() -> chatArea.appendText(msg.getSender() + ": " + msg.getContent() + "\n"));
                }
            }
        } catch (IOException e) {
            Platform.runLater(() -> showAlert("Connection Lost", "Disconnected from server."));
        }
    }

    private void sendMessage() {
        String text = inputField.getText().trim();
        if (!text.isEmpty()) {
            Message msg = new Message("message", username, text);
            out.println(gson.toJson(msg));
            chatArea.appendText("Me: " + text + "\n");
            inputField.clear();
        }
    }

    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
            alert.setTitle(title);
            alert.showAndWait();
        });
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
