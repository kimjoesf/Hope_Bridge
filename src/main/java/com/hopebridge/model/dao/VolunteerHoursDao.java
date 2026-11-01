package com.hopebridge.model.dao;

import com.hopebridge.model.db.SqlExecutor;
import com.hopebridge.model.dto.VolunteerSummary;
import com.hopebridge.model.entity.VolunteerShift;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Reads and writes the volunteer_hours table.
 */
public class VolunteerHoursDao {

    private final SqlExecutor sql;

    public VolunteerHoursDao(SqlExecutor sql) {
        this.sql = sql;
    }

    public void insert(VolunteerShift shift) {
        int id = sql.insert("""
                        INSERT INTO volunteer_hours (volunteer_id, event_id, work_date, hours, note)
                        VALUES (?, ?, ?, ?, ?)""",
                shift.getVolunteerId(), shift.getEventId(), shift.getWorkDate(), shift.getHours(), shift.getNote());
        shift.setId(id);
    }

    public List<VolunteerShift> findByVolunteer(int volunteerId) {
        return sql.query("""
                SELECT h.id, h.volunteer_id, h.event_id, h.work_date, h.hours, h.note, e.title AS event_title
                FROM volunteer_hours h
                LEFT JOIN events e ON e.id = h.event_id
                WHERE h.volunteer_id = ?
                ORDER BY h.work_date DESC, h.id DESC""", VolunteerHoursDao::map, volunteerId);
    }

    public VolunteerSummary summarize(int volunteerId) {
        return sql.queryOne("""
                SELECT ISNULL(SUM(hours), 0) AS total_hours, COUNT(*) AS shift_count,
                       MIN(work_date) AS first_day, MAX(work_date) AS last_day
                FROM volunteer_hours WHERE volunteer_id = ?""",
                rs -> new VolunteerSummary(rs.getBigDecimal("total_hours"), rs.getInt("shift_count"),
                        rs.getObject("first_day", LocalDate.class), rs.getObject("last_day", LocalDate.class)),
                volunteerId).orElseThrow();
    }

    private static VolunteerShift map(ResultSet rs) throws SQLException {
        VolunteerShift shift = new VolunteerShift();
        shift.setId(rs.getInt("id"));
        shift.setVolunteerId(rs.getInt("volunteer_id"));
        shift.setEventId(rs.getObject("event_id", Integer.class));
        shift.setEventTitle(rs.getString("event_title"));
        shift.setWorkDate(rs.getObject("work_date", LocalDate.class));
        shift.setHours(rs.getBigDecimal("hours"));
        shift.setNote(rs.getString("note"));
        return shift;
    }
}
