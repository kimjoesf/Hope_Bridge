package com.hopebridge.model.dao;

import com.hopebridge.model.db.SqlExecutor;
import com.hopebridge.model.entity.LookupItem;
import com.hopebridge.model.entity.LookupTable;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * One DAO for every lookup table.
 *
 * The table name comes from the LookupTable enum - never from the request - so it
 * is safe to place it in the SQL text. All the actual values still go through
 * PreparedStatement parameters.
 */
public class LookupDao {

    private final SqlExecutor sql;

    public LookupDao(SqlExecutor sql) {
        this.sql = sql;
    }

    public List<LookupItem> findAll(LookupTable table) {
        return sql.query(select(table) + " ORDER BY name", LookupDao::map);
    }

    public List<LookupItem> findActive(LookupTable table) {
        return sql.query(select(table) + " WHERE is_active = 1 ORDER BY name", LookupDao::map);
    }

    public Optional<LookupItem> findById(LookupTable table, int id) {
        return sql.queryOne(select(table) + " WHERE id = ?", LookupDao::map, id);
    }

    public boolean isActive(LookupTable table, int id) {
        return sql.scalar("SELECT COUNT(*) FROM " + table.getTableName() + " WHERE id = ? AND is_active = 1",
                Integer.class, id) > 0;
    }

    public int count(LookupTable table) {
        return sql.scalar("SELECT COUNT(*) FROM " + table.getTableName(), Integer.class);
    }

    public void insert(LookupTable table, LookupItem item) {
        int id = sql.insert("INSERT INTO " + table.getTableName() + " (name, description, is_active) VALUES (?, ?, ?)",
                item.getName(), item.getDescription(), item.isActive());
        item.setId(id);
    }

    public void update(LookupTable table, LookupItem item) {
        sql.update("UPDATE " + table.getTableName() + " SET name = ?, description = ?, is_active = ? WHERE id = ?",
                item.getName(), item.getDescription(), item.isActive(), item.getId());
    }

    public void delete(LookupTable table, int id) {
        sql.update("DELETE FROM " + table.getTableName() + " WHERE id = ?", id);
    }

    private static String select(LookupTable table) {
        return "SELECT id, name, description, is_active FROM " + table.getTableName();
    }

    private static LookupItem map(ResultSet rs) throws SQLException {
        return new LookupItem(rs.getInt("id"), rs.getString("name"), rs.getString("description"), rs.getBoolean("is_active"));
    }
}
