package com.hopebridge.model.dao;

import com.hopebridge.model.db.SqlExecutor;
import com.hopebridge.model.entity.DonorTier;
import com.hopebridge.model.entity.Role;
import com.hopebridge.model.entity.User;
import com.hopebridge.model.factory.CommunicationStrategyFactory;
import com.hopebridge.model.factory.UserFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Reads and writes the users table.
 */
public class UserDao {

    private static final String SELECT = """
            SELECT id, full_name, email, password_hash, role, phone, preferred_channel, donor_tier, created_at
            FROM users""";

    private final SqlExecutor sql;

    public UserDao(SqlExecutor sql) {
        this.sql = sql;
    }

    public Optional<User> findById(int id) {
        return sql.queryOne(SELECT + " WHERE id = ?", UserDao::map, id);
    }

    public Optional<User> findByEmail(String email) {
        return sql.queryOne(SELECT + " WHERE email = ?", UserDao::map, email);
    }

    public List<User> findByRole(Role role) {
        return sql.query(SELECT + " WHERE role = ? ORDER BY full_name", UserDao::map, role);
    }

    // UNUSED
    // public int countByRole(Role role) {
    //     return sql.scalar("SELECT COUNT(*) FROM users WHERE role = ?", Integer.class, role);
    // }

    public void insert(User user) {
        int id = sql.insert("""
                        INSERT INTO users (full_name, email, password_hash, role, phone, preferred_channel, donor_tier)
                        VALUES (?, ?, ?, ?, ?, ?, ?)""",
                user.getFullName(), user.getEmail(), user.getPasswordHash(), user.getRole(), user.getPhone(),
                user.getCommunicationStrategy().getCode(), user.getDonorTier());
        user.setId(id);
    }

    public void updateProfile(User user) {
        sql.update("UPDATE users SET full_name = ?, phone = ? WHERE id = ?",
                user.getFullName(), user.getPhone(), user.getId());
    }

    // We store the strategy by its code; the factory turns it back into an object when the user is loaded.
    public void updateChannel(User user) {
        sql.update("UPDATE users SET preferred_channel = ? WHERE id = ?",
                user.getCommunicationStrategy().getCode(), user.getId());
    }

    public void updateTier(int userId, DonorTier tier) {
        sql.update("UPDATE users SET donor_tier = ? WHERE id = ?", tier, userId);
    }

    // The role column decides the class (Factory), then the saved preference replaces
    // the class's default channel through the setter (Strategy swapped at runtime).
    private static User map(ResultSet rs) throws SQLException {
        User user = UserFactory.create(rs.getString("role"));
        user.setId(rs.getInt("id"));
        user.setFullName(rs.getString("full_name"));
        user.setEmail(rs.getString("email"));
        user.setPasswordHash(rs.getString("password_hash"));
        user.setPhone(rs.getString("phone"));
        user.setDonorTier(DonorTier.valueOf(rs.getString("donor_tier")));
        user.setCreatedAt(rs.getObject("created_at", LocalDateTime.class));
        user.setCommunicationStrategy(CommunicationStrategyFactory.create(rs.getString("preferred_channel")));
        return user;
    }
}