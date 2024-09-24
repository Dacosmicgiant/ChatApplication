package com.chatapp.client;

import com.chatapp.common.Message;
import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.*;
import java.net.Socket;
import java.net.ConnectException;
import java.net.SocketException;

public class ChatGUI extends Application {
    private TextArea chatArea; // Area to display chat messages
    private TextField inputField; // Input field for user to type messages
    private Button sendButton; // Button to send messages
    private VBox mainLayout; // Layout for the main chat UI
    private VBox loginLayout; // Layout for the login UI

    private Socket socket; // Socket for communication with the server
    private BufferedReader in; // Input stream to read messages from the server
    private PrintWriter out; // Output stream to send messages to the server
    private Gson gson; // Gson instance for JSON serialization/deserialization

    private String username; // Store the username of the logged-in user

    @Override
    public void start(Stage primaryStage) {
        gson = new Gson(); // Initialize Gson for JSON handling

        // Initialize the login UI and chat UI
        initializeLoginUI(primaryStage);
        initializeChatUI();

        // Handle application close event to close the socket connection
        primaryStage.setOnCloseRequest(e -> closeConnection());
    }

    // Method to set up the login UI
    private void initializeLoginUI(Stage primaryStage) {
        Label userLabel = new Label("Username:"); // Label for username input
        TextField userField = new TextField(); // Text field for username input
        Label passLabel = new Label("Password:"); // Label for password input
        PasswordField passField = new PasswordField(); // Password field for password input
        Button loginButton = new Button("Login"); // Button to initiate login

        // Arrange login elements in a vertical box layout
        loginLayout = new VBox(10, userLabel, userField, passLabel, passField, loginButton);
        loginLayout.setPadding(new Insets(20)); // Add padding around the layout

        Scene loginScene = new Scene(loginLayout, 300, 200); // Create scene for login UI
        primaryStage.setTitle("Login"); // Set window title
        primaryStage.setScene(loginScene); // Set the scene to the primary stage
        primaryStage.show(); // Show the primary stage

        // Event handler for the login button
        loginButton.setOnAction(e -> {
            String user = userField.getText().trim(); // Get username input
            String pass = passField.getText().trim(); // Get password input
            if (!user.isEmpty() && !pass.isEmpty()) { // Ensure inputs are not empty
                try {
                    connectToServer(); // Attempt to connect to the server
                    authenticate(user, pass, primaryStage); // Authenticate user credentials
                } catch (ConnectException ce) {
                    showAlert("Connection Error", "Unable to connect to server. Is the server running?"); // Handle
                                                                                                          // connection
                                                                                                          // error
                } catch (IOException ex) {
                    showAlert("Connection Error", "An error occurred while connecting to the server."); // Handle IO
                                                                                                        // error
                    ex.printStackTrace(); // Print stack trace for debugging
                }
            } else {
                showAlert("Input Error", "Please enter username and password."); // Alert for empty input
            }
        });
    }

    // Method to initialize the chat UI
    private void initializeChatUI() {
        chatArea = new TextArea(); // Text area to display chat messages
        chatArea.setEditable(false); // Make chat area non-editable
        inputField = new TextField(); // Input field for user messages
        sendButton = new Button("Send"); // Button to send messages

        // Arrange input field and button in a horizontal box layout
        HBox inputLayout = new HBox(10, inputField, sendButton);
        inputLayout.setPadding(new Insets(10)); // Add padding

        // Main layout for chat UI
        mainLayout = new VBox(10, chatArea, inputLayout);
        mainLayout.setPadding(new Insets(10)); // Add padding

        // Event handlers for sending messages
        sendButton.setOnAction(e -> sendMessage());
        inputField.setOnAction(e -> sendMessage());
    }

    // Method to establish connection to the chat server
    private void connectToServer() throws IOException {
        socket = new Socket("localhost", 12345); // Connect to server at localhost on port 12345
        in = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Input stream for server messages
        out = new PrintWriter(socket.getOutputStream(), true); // Output stream for sending messages to the server
    }

    // Method to authenticate the user with the server
    private void authenticate(String user, String pass, Stage primaryStage) {
        Message authMessage = new Message("auth", user, pass); // Create authentication message
        String authJson = gson.toJson(authMessage); // Convert message to JSON
        out.println(authJson); // Send authentication message to the server

        // Start a new thread to listen for authentication response
        new Thread(() -> {
            try {
                String response = in.readLine(); // Read response from the server
                if (response == null) {
                    Platform.runLater(() -> showAlert("Authentication Error", "No response from server.")); // Handle
                                                                                                            // null
                                                                                                            // response
                    return;
                }
                Message resp = gson.fromJson(response, Message.class); // Parse response JSON
                if ("auth_success".equals(resp.getType())) {
                    username = user; // Set username if authentication is successful
                    Platform.runLater(() -> setupChatUI(primaryStage)); // Setup chat UI
                    listenForMessages(); // Start listening for incoming messages
                } else {
                    Platform.runLater(() -> showAlert("Authentication Failed", resp.getContent())); // Handle
                                                                                                    // authentication
                                                                                                    // failure
                    closeConnection(); // Close connection
                }
            } catch (JsonSyntaxException | IOException e) {
                Platform.runLater(() -> showAlert("Authentication Error", "Failed to authenticate: " + e.getMessage())); // Handle
                                                                                                                         // authentication
                                                                                                                         // error
                e.printStackTrace(); // Print stack trace for debugging
                closeConnection(); // Close connection
            }
        }).start(); // Start the thread
    }

    // Method to setup the chat UI after successful authentication
    private void setupChatUI(Stage primaryStage) {
        Scene chatScene = new Scene(mainLayout, 600, 400); // Create scene for chat UI
        primaryStage.setTitle("Chat - " + username); // Update window title with username
        primaryStage.setScene(chatScene); // Set the scene to the primary stage
    }

    // Method to listen for incoming messages from the server
    private void listenForMessages() {
        new Thread(() -> {
            try {
                String message;
                // Continuously read messages from the server
                while ((message = in.readLine()) != null) {
                    try {
                        Message msg = gson.fromJson(message, Message.class); // Parse incoming message JSON
                        if ("message".equals(msg.getType()) || "system".equals(msg.getType())) {
                            // Format message and append to chat area
                            String finalMessage = msg.getSender() + ": " + msg.getContent();
                            Platform.runLater(() -> chatArea.appendText(finalMessage + "\n"));
                        }
                    } catch (JsonSyntaxException jse) {
                        System.err.println("Error parsing message: " + jse.getMessage()); // Handle parsing error
                    }
                }
            } catch (SocketException se) {
                // Handle socket exception for disconnection
                if (!socket.isClosed()) {
                    Platform.runLater(() -> showAlert("Connection Lost", "Disconnected from server."));
                }
            } catch (IOException e) {
                Platform.runLater(() -> showAlert("Error", "An error occurred while receiving messages.")); // Handle IO
                                                                                                            // error
                e.printStackTrace(); // Print stack trace for debugging
            } finally {
                closeConnection(); // Ensure the connection is closed
            }
        }).start(); // Start the thread
    }

    // Method to send a message to the server
    private void sendMessage() {
        String text = inputField.getText().trim(); // Get user input message
        if (!text.isEmpty()) { // Ensure the message is not empty
            Message msg = new Message("message", username, text); // Create message object
            out.println(gson.toJson(msg)); // Send message as JSON to the server
            chatArea.appendText("Me: " + text + "\n"); // Append sent message to chat area
            inputField.clear(); // Clear the input field
        }
    }

    // Method to display alerts to the user
    private void showAlert(String title, String message) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK); // Create an alert dialog
            alert.setTitle(title); // Set the title of the alert
            alert.showAndWait(); // Show the alert and wait for user action
        });
    }

    // Method to close the socket connection safely
    private void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close(); // Close the socket if it's not already closed
            }
        } catch (IOException ex) {
            System.err.println("Error closing socket: " + ex.getMessage()); // Handle closing error
        } finally {
            Platform.exit(); // Exit the JavaFX application
        }
    }

    // Main method to launch the application
    public static void main(String[] args) {
        launch(args); // Launch the JavaFX application
    }
}
