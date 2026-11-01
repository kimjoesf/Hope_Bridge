package com.hopebridge.model.entity;

import java.time.LocalDateTime;

/**
 * One message in a user's outbox, already shaped for the channel it travels on.
 */
public class Notification {

    private int id;
    private int userId;
    private String channel;
    private String subject;
    private String body;
    private boolean read;
    private LocalDateTime createdAt;

    public Notification() {
    }

    public Notification(int userId, String channel, String subject, String body) {
        this.userId = userId;
        this.channel = channel;
        this.subject = subject;
        this.body = body;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
