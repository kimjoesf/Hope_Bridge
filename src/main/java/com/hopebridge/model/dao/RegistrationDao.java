package com.hopebridge.model.dao;

import com.hopebridge.model.db.SqlExecutor;
import com.hopebridge.model.entity.EventRegistration;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Reads and writes the event_registrations table.
 */
public class RegistrationDao {

    private final SqlExecutor sql;

    public RegistrationDao(SqlExecutor sql) {
        this.sql = sql;
    }

    public void insert(int eventId, int userId, String ticketCode) {
        sql.update("INSERT INTO event_registrations (event_id, user_id, ticket_code) VALUES (?, ?, ?)",
                eventId, userId, ticketCode);
    }

    // The seat frees itself: registered_count is counted live from this table.
    public void delete(int eventId, int userId) {
        sql.update("DELETE FROM event_registrations WHERE event_id = ? AND user_id = ?", eventId, userId);
    }

    public boolean exists(int eventId, int userId) {
        return sql.scalar("SELECT COUNT(*) FROM event_registrations WHERE event_id = ? AND user_id = ?",
                Integer.class, eventId, userId) > 0;
    }

    public List<Integer> findAttendeeIds(int eventId) {
        return sql.query("SELECT user_id FROM event_registrations WHERE event_id = ?", rs -> rs.getInt(1), eventId);
    }

    public List<Integer> findEventIdsForUser(int userId) {
        return sql.query("SELECT event_id FROM event_registrations WHERE user_id = ?", rs -> rs.getInt(1), userId);
    }

    // We reuse the event columns and simply join the registration on top of them.
    public List<EventRegistration> findByUser(int userId) {
        String query = "SELECT " + EventDao.COLUMNS + ", reg.id AS registration_id, reg.ticket_code, reg.registered_at"
                + EventDao.FROM
                + " JOIN event_registrations reg ON reg.event_id = e.id"
                + " WHERE reg.user_id = ? ORDER BY e.starts_at";
        return sql.query(query, RegistrationDao::map, userId);
    }

    private static EventRegistration map(ResultSet rs) throws SQLException {
        EventRegistration registration = new EventRegistration();
        registration.setId(rs.getInt("registration_id"));
        registration.setTicketCode(rs.getString("ticket_code"));
        registration.setRegisteredAt(rs.getObject("registered_at", LocalDateTime.class));
        registration.setEvent(EventDao.map(rs));
        return registration;
    }
}
