// // src/com/chatapp/common/Message.java
// package com.chatapp.common;

// public class Message {
// private String type; // e.g., "auth", "message", "system"
// private String sender;
// private String content;

// public Message() {}

// public Message(String type, String sender, String content) {
// this.type = type;
// this.sender = sender;
// this.content = content;
// }

// // Getters and Setters
// public String getType() { return type; }
// public void setType(String type) { this.type = type; }

// public String getSender() { return sender; }
// public void setSender(String sender) { this.sender = sender; }

// public String getContent() { return content; }
// public void setContent(String content) { this.content = content; }
// }

// src/com/chatapp/common/Message.java
package com.chatapp.common;

public class Message {
    private String type; // e.g., "auth", "message", "system"
    private String sender;
    private String content;

    public Message() {
    }

    public Message(String type, String sender, String content) {
        this.type = type;
        this.sender = sender;
        this.content = content;
    }

    // Getters and Setters
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getSender() {
        return sender;
    }

    public void setSender(String sender) {
        this.sender = sender;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
