package com.firebase.notti.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Note {
    private String userId;
    private List<String> secodaryUserIds = new ArrayList<>();
    private String id;
    private String title;
    private boolean isFavorite = false;
    private boolean isGroup = false;

    private List<NoteMessage> messages = new ArrayList<>();

    public Note() {
        //this.id = UUID.randomUUID().toString();
        this.title = "No Title";
    } // Required for Firebase

    public Note(String userId, String title) {
        //this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.title = title;
    }

    public Note(String userId, String title, NoteMessage message) {
        this(userId,title);
        messages.add(message);
    }

    public Note(String userId, String title, List<NoteMessage> messages) {
        this(userId,title);
        this.messages.addAll(0,messages);
    }

    public boolean isGroup() {
        return isGroup;
    }

    public void setGroup(boolean group) {
        isGroup = group;
    }

    public boolean isFavorite() {
        return isFavorite;
    }

    public void setFavorite(boolean favorite) {
        isFavorite = favorite;
    }

    public void addMessage(NoteMessage message) {
        this.messages.add(message);
    }

    public List<NoteMessage> getMessages() {
        return messages;
    }

    public void setMessages(List<NoteMessage> messages) {
        this.messages = messages;
    }

    public void addSecondaryUser(String userId) {
        this.secodaryUserIds.add(userId);
    }

    public String getId() {
        return this.id;
    }
    public String getTitle() {
        return this.title;
    }
    public String getUserId() {
        return this.userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public List<String> getSecodaryUserIds() {
        return this.secodaryUserIds;
    }

    public void setSecodaryUserIds(List<String> secodaryUserIds) {
        this.secodaryUserIds = secodaryUserIds;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public NoteMessage getLastMessage() {
        if (messages == null || messages.isEmpty()) {
            return null;
        }
        return messages.get(messages.size() - 1);
    }
}
