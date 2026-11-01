package com.hopebridge.model.dao;

import com.hopebridge.model.db.SqlExecutor;
import com.hopebridge.model.entity.Event;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Reads and writes the events table.
 */
public class EventDao {

    // Kept in two pieces so RegistrationDao can add its own columns and joins to the same query.
    static final String COLUMNS = """
            e.id, e.title, e.summary, e.category_id, e.location, e.starts_at, e.capacity, e.created_by,
            e.updated_at, ec.name AS category_name,
            (SELECT COUNT(*) FROM event_registrations r WHERE r.event_id = e.id) AS registered_count""";
    static final String FROM = " FROM events e JOIN event_categories ec ON ec.id = e.category_id";

    private static final String SELECT = "SELECT " + COLUMNS + FROM;

    private final SqlExecutor sql;

    public EventDao(SqlExecutor sql) {
        this.sql = sql;
    }

    public List<Event> findUpcoming() {
        return sql.query(SELECT + " WHERE e.starts_at >= SYSDATETIME() ORDER BY e.starts_at", EventDao::map);
    }

    public List<Event> findAll() {
        return sql.query(SELECT + " ORDER BY e.starts_at DESC", EventDao::map);
    }

    public Optional<Event> findById(int id) {
        return sql.queryOne(SELECT + " WHERE e.id = ?", EventDao::map, id);
    }

    public void insert(Event event) {
        int id = sql.insert("""
                        INSERT INTO events (title, summary, category_id, location, starts_at, capacity, created_by)
                        VALUES (?, ?, ?, ?, ?, ?, ?)""",
                event.getTitle(), event.getSummary(), event.getCategoryId(), event.getLocation(),
                event.getStartsAt(), event.getCapacity(), event.getCreatedBy());
        event.setId(id);
    }

    public void update(Event event) {
        sql.update("""
                        UPDATE events
                        SET title = ?, summary = ?, category_id = ?, location = ?, starts_at = ?, capacity = ?,
                            updated_at = SYSDATETIME()
                        WHERE id = ?""",
                event.getTitle(), event.getSummary(), event.getCategoryId(), event.getLocation(),
                event.getStartsAt(), event.getCapacity(), event.getId());
    }

    // Shared with RegistrationDao, which joins the same event columns.
    static Event map(ResultSet rs) throws SQLException {
        Event event = new Event();
        event.setId(rs.getInt("id"));
        event.setTitle(rs.getString("title"));
        event.setSummary(rs.getString("summary"));
        event.setCategoryId(rs.getInt("category_id"));
        event.setCategoryName(rs.getString("category_name"));
        event.setLocation(rs.getString("location"));
        event.setStartsAt(rs.getObject("starts_at", LocalDateTime.class));
        event.setCapacity(rs.getInt("capacity"));
        event.setRegisteredCount(rs.getInt("registered_count"));
        event.setCreatedBy(rs.getInt("created_by"));
        event.setUpdatedAt(rs.getObject("updated_at", LocalDateTime.class));
        return event;
    }
}
