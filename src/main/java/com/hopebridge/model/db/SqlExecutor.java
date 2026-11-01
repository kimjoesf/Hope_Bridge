package com.hopebridge.model.db;

import com.hopebridge.model.exception.DataAccessException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * The boring JDBC part, written once.
 *
 * Opening a connection, binding parameters, looping over a ResultSet and
 * closing everything is the same in every DAO. Instead of copying that code
 * around, each DAO keeps one of these and only writes its SQL and its row mapping.
 *
 * Every query goes through a PreparedStatement, so user input is never
 * glued into SQL text.
 */
public final class SqlExecutor {

    private final Database database;

    public SqlExecutor(Database database) {
        this.database = database;
    }

    public <T> List<T> query(String sql, RowMapper<T> mapper, Object... params) {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = bind(connection.prepareStatement(sql), params);
             ResultSet rs = statement.executeQuery()) {
            List<T> rows = new ArrayList<>();
            while (rs.next()) {
                rows.add(mapper.map(rs));
            }
            return rows;
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    public <T> Optional<T> queryOne(String sql, RowMapper<T> mapper, Object... params) {
        List<T> rows = query(sql, mapper, params);
        return rows.isEmpty() ? Optional.empty() : Optional.of(rows.get(0));
    }

    // For single values such as COUNT(*) or SUM(...). Wrap SUMs in ISNULL(..., 0) so they never come back null.
    public <T> T scalar(String sql, Class<T> type, Object... params) {
        return queryOne(sql, rs -> rs.getObject(1, type), params).orElse(null);
    }

    public int update(String sql, Object... params) {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = bind(connection.prepareStatement(sql), params)) {
            return statement.executeUpdate();
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // Runs an INSERT and hands back the IDENTITY value SQL Server generated for the new row.
    public int insert(String sql, Object... params) {
        try (Connection connection = database.getConnection();
             PreparedStatement statement = bind(connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS), params)) {
            statement.executeUpdate();
            try (ResultSet keys = statement.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // All the statements succeed together, or none of them do.
    public void inTransaction(List<SqlStatement> statements) {
        try (Connection connection = database.getConnection()) {
            connection.setAutoCommit(false);
            try {
                for (SqlStatement each : statements) {
                    try (PreparedStatement statement = bind(connection.prepareStatement(each.sql()), each.params())) {
                        statement.executeUpdate();
                    }
                }
                connection.commit();
            } catch (SQLException e) {
                connection.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DataAccessException(e);
        }
    }

    // Enums are stored by their name; everything else (String, Integer, BigDecimal, LocalDate...) the driver handles itself.
    private static PreparedStatement bind(PreparedStatement statement, Object... params) throws SQLException {
        for (int i = 0; i < params.length; i++) {
            Object value = params[i] instanceof Enum<?> e ? e.name() : params[i];
            statement.setObject(i + 1, value);
        }
        return statement;
    }
}