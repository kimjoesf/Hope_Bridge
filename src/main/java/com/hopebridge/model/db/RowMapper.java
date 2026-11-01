package com.hopebridge.model.db;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Turns the current row of a ResultSet into one Java object.
 * Each DAO writes this part, because only the DAO knows its own columns.
 */
@FunctionalInterface
public interface RowMapper<T> {

    T map(ResultSet rs) throws SQLException;
}
