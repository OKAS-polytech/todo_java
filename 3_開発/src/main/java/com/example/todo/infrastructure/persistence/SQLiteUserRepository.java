package com.example.todo.infrastructure.persistence;

import com.example.todo.application.ports.out.UserRepositoryPort;
import com.example.todo.domain.models.User;
import java.sql.*;

/**
 * SQLiteを使用したユーザーリポジトリ。
 */
public class SQLiteUserRepository implements UserRepositoryPort {
    private final String url;

    public SQLiteUserRepository(String dbPath) {
        this.url = "jdbc:sqlite:" + dbPath;
        initializeDatabase();
    }

    private void initializeDatabase() {
        String sql = "CREATE TABLE IF NOT EXISTS users (id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE NOT NULL, password_hash TEXT NOT NULL)";
        try (Connection conn = DriverManager.getConnection(url); Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void save(User user) {
        String sql = "INSERT INTO users(username, password_hash) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, user.getUsername());
            pstmt.setString(2, user.getPasswordHash());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) user.setId(rs.getLong(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return new User(rs.getLong("id"), rs.getString("username"), rs.getString("password_hash"));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public User findById(Long id) {
        String sql = "SELECT * FROM users WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return new User(rs.getLong("id"), rs.getString("username"), rs.getString("password_hash"));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }
}
