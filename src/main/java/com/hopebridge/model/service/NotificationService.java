package com.hopebridge.model.service;

import com.hopebridge.model.dao.NotificationDao;
import com.hopebridge.model.dao.UserDao;
import com.hopebridge.model.entity.Notification;
import com.hopebridge.model.entity.Role;
import com.hopebridge.model.entity.User;

import java.util.List;

/**
 * The communication module's entry point: "send this to that person".
 *
 * Notice it never decides HOW to send. It asks the user to perform the contact,
 * and the user's current CommunicationStrategy shapes the message for its channel.
 */
public class NotificationService {

    private static final int INBOX_SIZE = 50;

    private final NotificationDao notificationDao;
    private final UserDao userDao;

    public NotificationService(NotificationDao notificationDao, UserDao userDao) {
        this.notificationDao = notificationDao;
        this.userDao = userDao;
    }

    public void send(User recipient, String subject, String body) {
        Notification notification = recipient.performContact(subject, body);
        notificationDao.insert(notification);
    }

    public void send(int userId, String subject, String body) {
        userDao.findById(userId).ifPresent(user -> send(user, subject, body));
    }

    public void sendToRole(Role role, String subject, String body) {
        userDao.findByRole(role).forEach(user -> send(user, subject, body));
    }

    public List<Notification> inbox(int userId) {
        return notificationDao.findByUser(userId, INBOX_SIZE);
    }

    public List<Notification> latest(int userId, int count) {
        return notificationDao.findByUser(userId, count);
    }

    public int unreadCount(int userId) {
        return notificationDao.countUnread(userId);
    }

    public void markAllRead(int userId) {
        notificationDao.markAllRead(userId);
    }
}
