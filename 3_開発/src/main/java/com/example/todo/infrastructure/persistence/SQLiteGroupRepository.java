package com.example.todo.infrastructure.persistence;

import com.example.todo.application.ports.out.GroupRepositoryPort;
import com.example.todo.domain.models.Group;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLiteを使用したグループリポジトリ。
 */
public class SQLiteGroupRepository implements GroupRepositoryPort {
    private final String url;

    public SQLiteGroupRepository(String dbPath) {
        this.url = "jdbc:sqlite:" + dbPath;
        initializeDatabase();
    }

    private void initializeDatabase() {
        String sql1 = "CREATE TABLE IF NOT EXISTS groups (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT NOT NULL)";
        String sql2 = "CREATE TABLE IF NOT EXISTS group_members (id INTEGER PRIMARY KEY AUTOINCREMENT, group_id INTEGER, user_id INTEGER, UNIQUE(group_id, user_id))";
        try (Connection conn = DriverManager.getConnection(url); Statement stmt = conn.createStatement()) {
            stmt.execute(sql1);
            stmt.execute(sql2);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void save(Group group) {
        String sql = "INSERT INTO groups(name) VALUES(?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, group.getName());
            pstmt.executeUpdate();
            try (ResultSet rs = pstmt.getGeneratedKeys()) {
                if (rs.next()) group.setId(rs.getLong(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void addMember(Long groupId, Long userId) {
        String sql = "INSERT OR IGNORE INTO group_members(group_id, user_id) VALUES(?, ?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, groupId);
            pstmt.setLong(2, userId);
            pstmt.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Group> findByUserId(Long userId) {
        String sql = "SELECT g.* FROM groups g JOIN group_members gm ON g.id = gm.group_id WHERE gm.user_id = ?";
        List<Group> groups = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    groups.add(new Group(rs.getLong("id"), rs.getString("name")));
                }
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return groups;
    }

    @Override
    public Group findById(Long id) {
        String sql = "SELECT * FROM groups WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) return new Group(rs.getLong("id"), rs.getString("name"));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return null;
    }

    @Override
    public boolean isMember(Long groupId, Long userId) {
        String sql = "SELECT 1 FROM group_members WHERE group_id = ? AND user_id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, groupId);
            pstmt.setLong(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}
