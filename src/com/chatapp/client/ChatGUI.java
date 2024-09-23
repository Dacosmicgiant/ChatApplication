
// package com.chatapp.client;

// import com.chatapp.common.Message;
// import com.google.gson.Gson;
// import javafx.application.Application;
// import javafx.application.Platform;
// import javafx.geometry.Insets;
// import javafx.scene.Scene;
// import javafx.scene.control.*;
// import javafx.scene.layout.*;
// import javafx.stage.Stage;

// import java.io.*;
// import java.net.Socket;

// public class ChatGUI extends Application {
//     private TextArea chatArea;
//     private TextField inputField;
//     private Button sendButton;
//     private VBox mainLayout; // Class-level for chat UI
//     private VBox loginLayout; // Class-level for login UI

//     private Socket socket;
//     private BufferedReader in;
//     private PrintWriter out;
//     private Gson gson;

//     private String username;

//     @Override
//     public void start(Stage primaryStage) {
//         gson = new Gson();

//         // Initialize Login UI
//         Label userLabel = new Label("Username:");
//         TextField userField = new TextField();
//         Label passLabel = new Label("Password:");
//         PasswordField passField = new PasswordField();
//         Button loginButton = new Button("Login");

//         loginLayout = new VBox(10, userLabel, userField, passLabel, passField, loginButton);
//         loginLayout.setPadding(new Insets(20));

//         Scene loginScene = new Scene(loginLayout, 300, 200);
//         primaryStage.setTitle("Login");
//         primaryStage.setScene(loginScene);
//         primaryStage.show();

//         loginButton.setOnAction(e -> {
//             String user = userField.getText().trim();
//             String pass = passField.getText().trim();
//             if (!user.isEmpty() && !pass.isEmpty()) {
//                 try {
//                     connectToServer();
//                     authenticate(user, pass, primaryStage);
//                 } catch (IOException ex) {
//                     showAlert("Connection Error", "Unable to connect to server.");
//                     ex.printStackTrace(); // Debug
//                 }
//             } else {
//                 showAlert("Input Error", "Please enter username and password.");
//             }
//         });

//         primaryStage.setOnCloseRequest(e -> {
//             try {
//                 if (socket != null && !socket.isClosed()) {
//                     socket.close();
//                 }
//             } catch (IOException ex) {
//                 // Ignore
//             }
//         });
//     }

//     private void connectToServer() throws IOException {
//         socket = new Socket("localhost", 12345);
//         in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//         out = new PrintWriter(socket.getOutputStream(), true);

//         // Start a listener thread
//         new Thread(this::listenForMessages).start();
//     }

//     private void authenticate(String user, String pass, Stage primaryStage) {
//         Message authMessage = new Message("auth", user, pass);
//         String authJson = gson.toJson(authMessage);
//         System.out.println("Sending auth message: " + authJson); // Debug
//         out.println(authJson);

//         try {
//             String response = in.readLine();
//             System.out.println("Received response: " + response); // Debug
//             if (response == null) {
//                 showAlert("Authentication Error", "No response from server.");
//                 return;
//             }
//             Message resp = gson.fromJson(response, Message.class);
//             if ("auth_success".equals(resp.getType())) {
//                 username = user;
//                 Platform.runLater(() -> {
//                     setupChatUI(primaryStage);
//                 });
//                 System.out.println("Authentication successful for user: " + user); // Debug
//             } else {
//                 showAlert("Authentication Failed", resp.getContent());
//                 System.out.println("Authentication failed: " + resp.getContent()); // Debug
//                 socket.close();
//             }
//         } catch (IOException e) {
//             showAlert("Authentication Error", "Failed to authenticate.");
//             System.err.println("Authentication error: " + e.getMessage()); // Debug
//             e.printStackTrace(); // Debug
//         }
//     }

//     private void setupChatUI(Stage primaryStage) {
//         // Initialize Main Chat UI
//         chatArea = new TextArea();
//         chatArea.setEditable(false);
//         inputField = new TextField();
//         sendButton = new Button("Send");

//         HBox inputLayout = new HBox(10, inputField, sendButton);
//         inputLayout.setPadding(new Insets(10));

//         mainLayout = new VBox(10, chatArea, inputLayout);
//         mainLayout.setPadding(new Insets(10));

//         sendButton.setOnAction(e -> sendMessage());
//         inputField.setOnAction(e -> sendMessage());

//         // Update the scene to chat UI
//         Scene chatScene = new Scene(mainLayout, 600, 400);
//         primaryStage.setTitle("Chat - " + username);
//         primaryStage.setScene(chatScene);
//     }

//     private void listenForMessages() {
//         String message;
//         try {
//             while ((message = in.readLine()) != null) {
//                 Message msg = gson.fromJson(message, Message.class);
//                 if ("message".equals(msg.getType()) || "system".equals(msg.getType())) {
//                     Platform.runLater(() -> chatArea.appendText(msg.getSender() + ": " + msg.getContent() + "\n"));
//                 }
//             }
//         } catch (IOException e) {
//             Platform.runLater(() -> showAlert("Connection Lost", "Disconnected from server."));
//             System.err.println("Connection lost: " + e.getMessage()); // Debug
//             e.printStackTrace(); // Debug
//         }
//     }

//     private void sendMessage() {
//         String text = inputField.getText().trim();
//         if (!text.isEmpty()) {
//             Message msg = new Message("message", username, text);
//             String msgJson = gson.toJson(msg);
//             System.out.println("Sending message: " + msgJson); // Debug
//             out.println(msgJson);
//             chatArea.appendText("Me: " + text + "\n");
//             inputField.clear();
//         }
//     }

//     private void showAlert(String title, String message) {
//         Platform.runLater(() -> {
//             Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
//             alert.setTitle(title);
//             alert.showAndWait();
//         });
//     }

//     @Override
//     public void stop() throws Exception {
//         super.stop();
//         if (socket != null && !socket.isClosed()) {
//             socket.close();
//         }
//     }

//     public static void main(String[] args) {
//         launch(args);
//     }
// }

// package com.chatapp.client;

// import com.chatapp.common.Message;
// import com.google.gson.Gson;
// import com.google.gson.JsonSyntaxException;
// import javafx.application.Application;
// import javafx.application.Platform;
// import javafx.geometry.Insets;
// import javafx.scene.Scene;
// import javafx.scene.control.*;
// import javafx.scene.layout.*;
// import javafx.stage.Stage;

// import java.io.*;
// import java.net.Socket;
// import java.net.ConnectException;

// public class ChatGUI extends Application {
//     private TextArea chatArea;
//     private TextField inputField;
//     private Button sendButton;
//     private VBox mainLayout;
//     private VBox loginLayout;

//     private Socket socket;
//     private BufferedReader in;
//     private PrintWriter out;
//     private Gson gson;

//     private String username;

//     @Override
//     public void start(Stage primaryStage) {
//         gson = new Gson();

//         // Initialize Login UI
//         Label userLabel = new Label("Username:");
//         TextField userField = new TextField();
//         Label passLabel = new Label("Password:");
//         PasswordField passField = new PasswordField();
//         Button loginButton = new Button("Login");

//         loginLayout = new VBox(10, userLabel, userField, passLabel, passField, loginButton);
//         loginLayout.setPadding(new Insets(20));

//         Scene loginScene = new Scene(loginLayout, 300, 200);
//         primaryStage.setTitle("Login");
//         primaryStage.setScene(loginScene);
//         primaryStage.show();

//         loginButton.setOnAction(e -> {
//             String user = userField.getText().trim();
//             String pass = passField.getText().trim();
//             if (!user.isEmpty() && !pass.isEmpty()) {
//                 try {
//                     connectToServer();
//                     authenticate(user, pass, primaryStage);
//                 } catch (ConnectException ce) {
//                     showAlert("Connection Error", "Unable to connect to server. Is the server running?");
//                     System.err.println("Connection error: " + ce.getMessage());
//                 } catch (IOException ex) {
//                     showAlert("Connection Error", "An error occurred while connecting to the server.");
//                     System.err.println("IO error: " + ex.getMessage());
//                     ex.printStackTrace();
//                 }
//             } else {
//                 showAlert("Input Error", "Please enter username and password.");
//             }
//         });

//         primaryStage.setOnCloseRequest(e -> {
//             try {
//                 if (socket != null && !socket.isClosed()) {
//                     socket.close();
//                 }
//             } catch (IOException ex) {
//                 System.err.println("Error closing socket: " + ex.getMessage());
//             }
//         });
//     }

//     private void connectToServer() throws IOException {
//         System.out.println("Attempting to connect to server...");
//         socket = new Socket("localhost", 12345);
//         in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
//         out = new PrintWriter(socket.getOutputStream(), true);
//         System.out.println("Connected to server successfully.");

//         // Start a listener thread
//         new Thread(this::listenForMessages).start();
//     }

//     private void authenticate(String user, String pass, Stage primaryStage) {
//         Message authMessage = new Message("auth", user, pass);
//         String authJson = gson.toJson(authMessage);
//         System.out.println("Sending auth message: " + authJson);
//         out.println(authJson);

//         try {
//             String response = in.readLine();
//             System.out.println("Received response: " + response);
//             if (response == null) {
//                 showAlert("Authentication Error", "No response from server.");
//                 return;
//             }
//             try {
//                 Message resp = gson.fromJson(response, Message.class);
//                 if ("auth_success".equals(resp.getType())) {
//                     username = user;
//                     Platform.runLater(() -> {
//                         setupChatUI(primaryStage);
//                     });
//                     System.out.println("Authentication successful for user: " + user);
//                 } else {
//                     showAlert("Authentication Failed", resp.getContent());
//                     System.out.println("Authentication failed: " + resp.getContent());
//                     socket.close();
//                 }
//             } catch (JsonSyntaxException jse) {
//                 showAlert("Server Error", "Received invalid response from server.");
//                 System.err.println("JSON parsing error: " + jse.getMessage());
//                 socket.close();
//             }
//         } catch (IOException e) {
//             showAlert("Authentication Error", "Failed to authenticate: " + e.getMessage());
//             System.err.println("Authentication error: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }

//     private void setupChatUI(Stage primaryStage) {
//         // Initialize Main Chat UI
//         chatArea = new TextArea();
//         chatArea.setEditable(false);
//         inputField = new TextField();
//         sendButton = new Button("Send");

//         HBox inputLayout = new HBox(10, inputField, sendButton);
//         inputLayout.setPadding(new Insets(10));

//         mainLayout = new VBox(10, chatArea, inputLayout);
//         mainLayout.setPadding(new Insets(10));

//         sendButton.setOnAction(e -> sendMessage());
//         inputField.setOnAction(e -> sendMessage());

//         // Update the scene to chat UI
//         Scene chatScene = new Scene(mainLayout, 600, 400);
//         primaryStage.setTitle("Chat - " + username);
//         primaryStage.setScene(chatScene);
//     }

//     private void listenForMessages() {
//         String message;
//         try {
//             while ((message = in.readLine()) != null) {
//                 try {
//                     Message msg = gson.fromJson(message, Message.class);
//                     if ("message".equals(msg.getType()) || "system".equals(msg.getType())) {
//                         Platform.runLater(() -> chatArea.appendText(msg.getSender() + ": " + msg.getContent() + "\n"));
//                     }
//                 } catch (JsonSyntaxException jse) {
//                     System.err.println("Error parsing message: " + jse.getMessage());
//                     System.err.println("Raw message: " + message);
//                 }
//             }
//         } catch (IOException e) {
//             Platform.runLater(() -> showAlert("Connection Lost", "Disconnected from server: " + e.getMessage()));
//             System.err.println("Connection lost: " + e.getMessage());
//             e.printStackTrace();
//         }
//     }

//     private void sendMessage() {
//         String text = inputField.getText().trim();
//         if (!text.isEmpty()) {
//             Message msg = new Message("message", username, text);
//             String msgJson = gson.toJson(msg);
//             System.out.println("Sending message: " + msgJson);
//             out.println(msgJson);
//             chatArea.appendText("Me: " + text + "\n");
//             inputField.clear();
//         }
//     }

//     private void showAlert(String title, String message) {
//         Platform.runLater(() -> {
//             Alert alert = new Alert(Alert.AlertType.INFORMATION, message, ButtonType.OK);
//             alert.setTitle(title);
//             alert.showAndWait();
//         });
//     }

//     @Override
//     public void stop() throws Exception {
//         super.stop();
//         System.out.println("Application stopping, closing resources...");
//         if (socket != null && !socket.isClosed()) {
//             socket.close();
//         }
//         System.out.println("Resources closed.");
//     }

//     public static void main(String[] args) {
//         launch(args);
//     }
// }

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
    private TextArea chatArea;
    private TextField inputField;
    private Button sendButton;
    private VBox mainLayout; // Class-level for chat UI
    private VBox loginLayout; // Class-level for login UI

    private Socket socket;
    private BufferedReader in;
    private PrintWriter out;
    private Gson gson;

    private String username;

    @Override
    public void start(Stage primaryStage) {
        gson = new Gson();

        initializeLoginUI(primaryStage);
        initializeChatUI();

        primaryStage.setOnCloseRequest(e -> closeConnection());
    }

    private void initializeLoginUI(Stage primaryStage) {
        Label userLabel = new Label("Username:");
        TextField userField = new TextField();
        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();
        Button loginButton = new Button("Login");

        loginLayout = new VBox(10, userLabel, userField, passLabel, passField, loginButton);
        loginLayout.setPadding(new Insets(20));

        Scene loginScene = new Scene(loginLayout, 300, 200);
        primaryStage.setTitle("Login");
        primaryStage.setScene(loginScene);
        primaryStage.show();

        loginButton.setOnAction(e -> {
            String user = userField.getText().trim();
            String pass = passField.getText().trim();
            if (!user.isEmpty() && !pass.isEmpty()) {
                try {
                    connectToServer();
                    authenticate(user, pass, primaryStage);
                } catch (ConnectException ce) {
                    showAlert("Connection Error", "Unable to connect to server. Is the server running?");
                } catch (IOException ex) {
                    showAlert("Connection Error", "An error occurred while connecting to the server.");
                    ex.printStackTrace();
                }
            } else {
                showAlert("Input Error", "Please enter username and password.");
            }
        });
    }

    private void initializeChatUI() {
        chatArea = new TextArea();
        chatArea.setEditable(false);
        inputField = new TextField();
        sendButton = new Button("Send");

        HBox inputLayout = new HBox(10, inputField, sendButton);
        inputLayout.setPadding(new Insets(10));

        mainLayout = new VBox(10, chatArea, inputLayout);
        mainLayout.setPadding(new Insets(10));

        sendButton.setOnAction(e -> sendMessage());
        inputField.setOnAction(e -> sendMessage());
    }

    private void connectToServer() throws IOException {
        socket = new Socket("localhost", 12345);
        in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        out = new PrintWriter(socket.getOutputStream(), true);
    }

    private void authenticate(String user, String pass, Stage primaryStage) {
        Message authMessage = new Message("auth", user, pass);
        String authJson = gson.toJson(authMessage);
        out.println(authJson);

        new Thread(() -> {
            try {
                String response = in.readLine();
                if (response == null) {
                    Platform.runLater(() -> showAlert("Authentication Error", "No response from server."));
                    return;
                }
                Message resp = gson.fromJson(response, Message.class);
                if ("auth_success".equals(resp.getType())) {
                    username = user;
                    Platform.runLater(() -> setupChatUI(primaryStage));
                    listenForMessages();
                } else {
                    Platform.runLater(() -> showAlert("Authentication Failed", resp.getContent()));
                    closeConnection();
                }
            } catch (JsonSyntaxException | IOException e) {
                Platform.runLater(() -> showAlert("Authentication Error", "Failed to authenticate: " + e.getMessage()));
                e.printStackTrace();
                closeConnection();
            }
        }).start();
    }

    private void setupChatUI(Stage primaryStage) {
        Scene chatScene = new Scene(mainLayout, 600, 400);
        primaryStage.setTitle("Chat - " + username);
        primaryStage.setScene(chatScene);
    }

    private void listenForMessages() {
        new Thread(() -> {
            try {
                String message;
                while ((message = in.readLine()) != null) {
                    try {
                        Message msg = gson.fromJson(message, Message.class);
                        if ("message".equals(msg.getType()) || "system".equals(msg.getType())) {
                            String finalMessage = msg.getSender() + ": " + msg.getContent();
                            Platform.runLater(() -> chatArea.appendText(finalMessage + "\n"));
                        }
                    } catch (JsonSyntaxException jse) {
                        System.err.println("Error parsing message: " + jse.getMessage());
                    }
                }
            } catch (SocketException se) {
                if (!socket.isClosed()) {
                    Platform.runLater(() -> showAlert("Connection Lost", "Disconnected from server."));
                }
            } catch (IOException e) {
                Platform.runLater(() -> showAlert("Error", "An error occurred while receiving messages."));
                e.printStackTrace();
            } finally {
                closeConnection();
            }
        }).start();
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

    private void closeConnection() {
        try {
            if (socket != null && !socket.isClosed()) {
                socket.close();
            }
        } catch (IOException ex) {
            System.err.println("Error closing socket: " + ex.getMessage());
        }
    }

    @Override
    public void stop() {
        closeConnection();
    }

    public static void main(String[] args) {
        launch(args);
    }
}