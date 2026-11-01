package com.hopebridge.model.exception;

import java.sql.SQLException;

/**
 * Wraps a SQLException so the rest of the app does not have to catch checked
 * database errors everywhere. Services can still ask what kind of problem it was.
 */
public class DataAccessException extends RuntimeException {

    // SQL Server error numbers we care about
    private static final int FOREIGN_KEY_VIOLATION = 547;
    private static final int UNIQUE_INDEX_VIOLATION = 2601;
    private static final int UNIQUE_CONSTRAINT_VIOLATION = 2627;

    private final int errorCode;

    public DataAccessException(SQLException cause) {
        super(cause.getMessage(), cause);
        this.errorCode = cause.getErrorCode();
    }

    // The row is still referenced somewhere else (e.g. a category used by a donation).
    public boolean isForeignKeyViolation() {
        return errorCode == FOREIGN_KEY_VIOLATION;
    }

    // Someone tried to insert a value that must be unique (e.g. an email that already exists).
    public boolean isUniqueViolation() {
        return errorCode == UNIQUE_INDEX_VIOLATION || errorCode == UNIQUE_CONSTRAINT_VIOLATION;
    }
}
