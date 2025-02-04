package com.firebase.notti.model;

import java.util.UUID;

public class Note {
    private String userId;
    private String id;
    private String title;
    private String lastMessage;
    private long timestamp;

    public Note() { } // Required for Firebase

    public Note(String id, String title, String lastMessage, long timestamp) {
        this.userId = UUID.randomUUID().toString();
        this.id = id;
        this.title = title;
        this.lastMessage = lastMessage;
        this.timestamp = timestamp;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getLastMessage() { return lastMessage; }
    public long getTimestamp() { return timestamp; }

    public String getUserId() { return userId; }
}
