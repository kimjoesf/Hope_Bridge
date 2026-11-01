package com.hopebridge.model.dao;

import com.hopebridge.model.db.SqlExecutor;
import com.hopebridge.model.entity.Notification;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Reads and writes the notifications table (every user's message outbox).
 */
public class NotificationDao {

    private final SqlExecutor sql;

    public NotificationDao(SqlExecutor sql) {
        this.sql = sql;
    }

    public void insert(Notification notification) {
        int id = sql.insert("INSERT INTO notifications (user_id, channel, subject, body) VALUES (?, ?, ?, ?)",
                notification.getUserId(), notification.getChannel(), notification.getSubject(), notification.getBody());
        notification.setId(id);
    }

    public List<Notification> findByUser(int userId, int limit) {
        return sql.query("""
                SELECT id, user_id, channel, subject, body, is_read, created_at
                FROM notifications WHERE user_id = ?
                ORDER BY created_at DESC, id DESC
                OFFSET 0 ROWS FETCH NEXT ? ROWS ONLY""", NotificationDao::map, userId, limit);
    }

    public int countUnread(int userId) {
        return sql.scalar("SELECT COUNT(*) FROM notifications WHERE user_id = ? AND is_read = 0", Integer.class, userId);
    }

    public void markAllRead(int userId) {
        sql.update("UPDATE notifications SET is_read = 1 WHERE user_id = ? AND is_read = 0", userId);
    }

    private static Notification map(ResultSet rs) throws SQLException {
        Notification notification = new Notification();
        notification.setId(rs.getInt("id"));
        notification.setUserId(rs.getInt("user_id"));
        notification.setChannel(rs.getString("channel"));
        notification.setSubject(rs.getString("subject"));
        notification.setBody(rs.getString("body"));
        notification.setRead(rs.getBoolean("is_read"));
        notification.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        return notification;
    }
}