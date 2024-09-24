package com.chatapp.common;

/**
 * The Message class represents a message object with a type, sender, and
 * content.
 * This can be used to transfer different types of messages (e.g.,
 * authentication, chat messages, or system notifications)
 * between clients and servers in the chat application.
 */
public class Message {

    // Type of the message (e.g., "auth" for authentication, "message" for chat,
    // "system" for system notifications)
    private String type;

    // The sender of the message (e.g., username or system identifier)
    private String sender;

    // The content of the message (e.g., actual chat message, system alert, etc.)
    private String content;

    /**
     * Default constructor for Message. Initializes an empty Message object.
     * Useful when creating a message instance without pre-defined values.
     */
    public Message() {
    }

    /**
     * Parameterized constructor for Message.
     * Initializes a Message object with specified type, sender, and content.
     *
     * @param type    The type of the message (e.g., "auth", "message", "system").
     * @param sender  The sender of the message (e.g., a username or system
     *                identifier).
     * @param content The content of the message (e.g., text content or system
     *                message).
     */
    public Message(String type, String sender, String content) {
        this.type = type;
        this.sender = sender;
        this.content = content;
    }

    // Getter for message type
    public String getType() {
        return type;
    }

    // Setter for message type
    public void setType(String type) {
        this.type = type;
    }

    // Getter for sender information
    public String getSender() {
        return sender;
    }

    // Setter for sender information
    public void setSender(String sender) {
        this.sender = sender;
    }

    // Getter for message content
    public String getContent() {
        return content;
    }

    // Setter for message content
    public void setContent(String content) {
        this.content = content;
    }
}
