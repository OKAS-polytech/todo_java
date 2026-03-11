package com.example.todo.infrastructure.persistence;

import com.example.todo.application.dtos.SearchCriteria;
import com.example.todo.application.ports.out.TaskRepositoryPort;
import com.example.todo.domain.models.Priority;
import com.example.todo.domain.models.Status;
import com.example.todo.domain.models.Task;

import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLiteを使用したタスクリポジトリの実装。
 */
public class SQLiteTaskRepository implements TaskRepositoryPort {

    private final String url;
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public SQLiteTaskRepository(String dbPath) {
        this.url = "jdbc:sqlite:" + dbPath;
        initializeDatabase();
    }

    private void initializeDatabase() {
        String sql = """
            CREATE TABLE IF NOT EXISTS tasks (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                title TEXT NOT NULL,
                content TEXT,
                due_date TEXT,
                priority TEXT,
                status TEXT,
                created_at TEXT
            );
            """;
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
        } catch (SQLException e) {
            throw new RuntimeException("DB初期化失敗", e);
        }
    }

    @Override
    public void save(Task task) {
        if (task.getId() == null) {
            insert(task);
        } else {
            update(task);
        }
    }

    private void insert(Task task) {
        String sql = "INSERT INTO tasks(title, content, due_date, priority, status, created_at) VALUES(?,?,?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getContent());
            pstmt.setString(3, task.getDueDate() != null ? task.getDueDate().format(formatter) : null);
            pstmt.setString(4, task.getPriority().name());
            pstmt.setString(5, task.getStatus().name());
            pstmt.setString(6, task.getCreatedAt().format(formatter));
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    task.setId(generatedKeys.getLong(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("保存失敗", e);
        }
    }

    private void update(Task task) {
        String sql = "UPDATE tasks SET title=?, content=?, due_date=?, priority=?, status=? WHERE id=?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, task.getTitle());
            pstmt.setString(2, task.getContent());
            pstmt.setString(3, task.getDueDate() != null ? task.getDueDate().format(formatter) : null);
            pstmt.setString(4, task.getPriority().name());
            pstmt.setString(5, task.getStatus().name());
            pstmt.setLong(6, task.getId());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("更新失敗", e);
        }
    }

    @Override
    public Task findById(Long id) {
        String sql = "SELECT * FROM tasks WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToTask(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("検索失敗", e);
        }
        return null;
    }

    @Override
    public List<Task> findAll(SearchCriteria criteria) {
        StringBuilder sql = new StringBuilder("SELECT * FROM tasks WHERE 1=1");
        List<Object> params = new ArrayList<>();

        if (criteria.filterStatus() != null && !criteria.filterStatus().isEmpty()) {
            sql.append(" AND status = ?");
            params.add(criteria.filterStatus());
        }
        if (criteria.filterPriority() != null && !criteria.filterPriority().isEmpty()) {
            sql.append(" AND priority = ?");
            params.add(criteria.filterPriority());
        }
        if (criteria.keyword() != null && !criteria.keyword().isEmpty()) {
            sql.append(" AND (title LIKE ? OR content LIKE ?)");
            String k = "%" + criteria.keyword() + "%";
            params.add(k);
            params.add(k);
        }

        if (criteria.sortField() != null && !criteria.sortField().isEmpty()) {
            // SQLインジェクション対策のため、ソートフィールド名はホワイトリストでチェックすべきだが、簡易実装として
            String order = switch (criteria.sortField().toLowerCase()) {
                case "due_date" -> "due_date ASC";
                case "priority" -> "CASE priority WHEN 'HIGH' THEN 1 WHEN 'MEDIUM' THEN 2 WHEN 'LOW' THEN 3 END";
                case "created_at" -> "created_at DESC";
                default -> "created_at DESC";
            };
            sql.append(" ORDER BY ").append(order);
        } else {
            sql.append(" ORDER BY created_at DESC");
        }

        List<Task> tasks = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                pstmt.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    tasks.add(mapResultSetToTask(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("全件取得失敗", e);
        }
        return tasks;
    }

    @Override
    public void delete(Long id) {
        String sql = "DELETE FROM tasks WHERE id = ?";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, id);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("削除失敗", e);
        }
    }

    @Override
    public int deleteCompleted() {
        String sql = "DELETE FROM tasks WHERE status = 'DONE'";
        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("一括削除失敗", e);
        }
    }

    private Task mapResultSetToTask(ResultSet rs) throws SQLException {
        return new Task(
            rs.getLong("id"),
            rs.getString("title"),
            rs.getString("content"),
            rs.getString("due_date") != null ? LocalDateTime.parse(rs.getString("due_date"), formatter) : null,
            Priority.valueOf(rs.getString("priority")),
            Status.valueOf(rs.getString("status")),
            LocalDateTime.parse(rs.getString("created_at"), formatter)
        );
    }
}
