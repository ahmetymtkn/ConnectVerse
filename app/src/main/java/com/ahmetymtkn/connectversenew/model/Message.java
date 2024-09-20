package com.ahmetymtkn.connectversenew.model;

public class Message {
    private String senderID;
    private String receiverID;
    private String message;
    private long timestamp;

    // Boş Constructor (Firebase'in serileştirmesi için gerekli)
    public Message() {}

    // Parametreli Constructor
    public Message(String senderID, String receiverID, String message, long timestamp) {
        this.senderID = senderID;
        this.receiverID = receiverID;
        this.message = message;
        this.timestamp = timestamp;
    }

    // Getter ve Setter metodları
    public String getSenderID() {
        return senderID;
    }

    public void setSenderID(String senderID) {
        this.senderID = senderID;
    }

    public String getReceiverID() {
        return receiverID;
    }

    public void setReceiverID(String receiverID) {
        this.receiverID = receiverID;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}

