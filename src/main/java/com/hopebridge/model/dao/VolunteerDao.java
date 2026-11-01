package com.hopebridge.model.dao;

import com.hopebridge.model.db.SqlExecutor;
import com.hopebridge.model.db.SqlStatement;

import java.util.ArrayList;
import java.util.List;

/**
 * Reads and writes what a volunteer can do and when: the volunteer_skills and volunteer_availability link tables.
 */
public class VolunteerDao {

    private final SqlExecutor sql;

    public VolunteerDao(SqlExecutor sql) {
        this.sql = sql;
    }

    public List<Integer> findSkillIds(int userId) {
        return sql.query("SELECT skill_id FROM volunteer_skills WHERE user_id = ?", rs -> rs.getInt(1), userId);
    }

    public List<Integer> findSlotIds(int userId) {
        return sql.query("SELECT slot_id FROM volunteer_availability WHERE user_id = ?", rs -> rs.getInt(1), userId);
    }

    public List<String> findSkillNames(int userId) {
        return sql.query("""
                SELECT s.name FROM volunteer_skills vs JOIN skills s ON s.id = vs.skill_id
                WHERE vs.user_id = ? ORDER BY s.name""", rs -> rs.getString(1), userId);
    }

    // Clear the old picks and save the new ones in one transaction, so a failure never leaves half a profile.
    public void replaceProfile(int userId, List<Integer> skillIds, List<Integer> slotIds) {
        List<SqlStatement> statements = new ArrayList<>();
        statements.add(new SqlStatement("DELETE FROM volunteer_skills WHERE user_id = ?", userId));
        statements.add(new SqlStatement("DELETE FROM volunteer_availability WHERE user_id = ?", userId));
        skillIds.forEach(id -> statements.add(
                new SqlStatement("INSERT INTO volunteer_skills (user_id, skill_id) VALUES (?, ?)", userId, id)));
        slotIds.forEach(id -> statements.add(
                new SqlStatement("INSERT INTO volunteer_availability (user_id, slot_id) VALUES (?, ?)", userId, id)));
        sql.inTransaction(statements);
    }
}
