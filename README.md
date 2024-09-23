# ChatApp - JavaFX Chat Client

## Table of Contents

- [Project Description](#project-description)
- [Features](#features)
- [Technologies](#technologies)
- [Installation](#installation)
- [Usage](#usage)
- [Troubleshooting](#troubleshooting)
- [Contributing](#contributing)
- [License](#license)

---

## Project Description

ChatApp is a simple chat client built using JavaFX for the frontend and Gson for handling JSON data. It allows users to communicate with a server, sending and receiving messages in real-time. This project demonstrates Java networking, JSON parsing, and a basic GUI using JavaFX.

## Features

- Real-time messaging via TCP/IP.
- Interactive user interface with JavaFX.
- JSON message formatting with Gson.
- Lightweight and easy-to-setup.

---

## Technologies

- **Java 17**: Core language used for the application.
- **JavaFX 17**: For building the GUI.
- **Gson 2.8.9**: For JSON serialization/deserialization.
- **TCP/IP**: Networking protocol used for communication.

---

## Installation

### Prerequisites

- Ensure you have **Java 17** or higher installed.
- **JavaFX SDK 17** or higher must be downloaded and configured.
- The `gson-2.8.9.jar` file must be available in the `lib` directory.

### Steps

1. Clone the repository:
   ```bash
   git clone https://github.com/your-username/ChatApp.git
   cd ChatApp
   ```
2. Download and configure JavaFX SDK:

   - Download the JavaFX SDK from the official site: JavaFX SDK
   - Extract the SDK and note the path for later use.

3. Download Gson library:

   - Download gson-2.8.9.jar from Maven Central
   - Place the .jar file in the lib folder.

4. Compile the project:

   javac --module-path /path-to-javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp .;./lib/gson-2.8.9.jar com/chatapp/client/ChatGUI.java

5. Run the application:

   java --module-path /path-to-javafx-sdk/lib --add-modules javafx.controls,javafx.fxml -cp .;./lib/gson-2.8.9.jar com.chatapp.client.ChatGUI

## Usage

1. Launch the ChatApp by running the command provided in the Installation section.

2. The chat interface will open, allowing you to connect to a chat server.

3. Type a message in the input box and press Send to communicate with other users connected to the server.

## Troubleshooting

Common Issues

1. JavaFX runtime errors: Ensure that the JavaFX SDK path is correctly set when running the application.

2. Gson library not found: Make sure the gson-2.8.9.jar is located in the lib directory and referenced in the -cp (classpath) argument during execution.

3. Server connection issues: Verify that the chat server is running and the correct IP and port are being used.

License
This project is licensed under the MIT License. See the LICENSE file for more information.

### Key Points:

- Ensure paths for JavaFX and Gson are correct in both `javac` and `java` commands.
- Adapt the GitHub repository link and JavaFX path according to your setup.
