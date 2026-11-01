package com.hopebridge.model.db;

import com.hopebridge.config.AppConfig;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * SINGLETON - the one and only gateway to SQL Server.
 *
 * Every DAO asks this same object for a connection, so the connection details
 * live in exactly one place - and so does the connection pool.
 *
 * Why a pool? Opening an encrypted connection to SQL Server is slow, and a page
 * may run a dozen queries. The pool keeps a few connections open and lends them
 * out; "closing" one just hands it back. Having two pools would defeat the
 * purpose, which is exactly why this class must have a single instance.
 *
 * Each caller still gets its own Connection for the duration of a query: many
 * requests run at the same time, and a JDBC connection is not meant to be used by
 * two threads at once.
 */
public final class Database {

    private static final int MAX_CONNECTIONS = 10;

    private static Database instance;

    private final HikariDataSource pool;

    private Database() {
        AppConfig config = AppConfig.getInstance();
        HikariConfig settings = new HikariConfig();
        settings.setJdbcUrl(config.get("db.url"));
        // With Windows authentication there is no user name, so we only pass credentials when they exist.
        if (!config.get("db.user").isEmpty()) {
            settings.setUsername(config.get("db.user"));
            settings.setPassword(config.get("db.password"));
        }
        settings.setMaximumPoolSize(MAX_CONNECTIONS);
        settings.setPoolName("hopebridge-db");
        // Fail fast with a clear message at startup if the database cannot be reached.
        settings.setInitializationFailTimeout(10_000);
        this.pool = new HikariDataSource(settings);
    }

    public static synchronized Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        return pool.getConnection();
    }
}
