package com.firebase.notti.model;

public class NoteMessage {

    private String userId;
    private Object message;
    private String messageType;

    public NoteMessage(String userId, Object message, String messageType, long timestamp) {
        this.userId = userId;
        this.message = message;
        this.messageType = messageType;
        this.timestamp = timestamp;
    }

    private long timestamp;

    public NoteMessage(Object message) {
        this.message = message;
    }

    public String getMessageType() {
        return messageType;
    }

    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public Object getMessage() {
        return message;
    }

    public void setMessage(Object message) {
        this.message = message;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
