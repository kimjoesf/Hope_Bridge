package com.hopebridge.model.db;

/**
 * One SQL command with its parameters, so a group of them can run inside a single transaction.
 */
public record SqlStatement(String sql, Object... params) {
}
