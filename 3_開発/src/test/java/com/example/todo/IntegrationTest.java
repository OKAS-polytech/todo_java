package com.example.todo;

import com.example.todo.application.dtos.CreateTaskCommand;
import com.example.todo.application.dtos.SearchCriteria;
import com.example.todo.application.dtos.TaskDTO;
import com.example.todo.application.services.TaskService;
import com.example.todo.domain.models.Priority;
import com.example.todo.infrastructure.persistence.SQLiteTaskRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * サービス層とリポジトリ層（実DB）の連携を確認する結合テスト。
 */
public class IntegrationTest {

    private static final String DB_FILE = "it_todo.db";
    private TaskService service;

    @BeforeEach
    void setUp() {
        SQLiteTaskRepository repository = new SQLiteTaskRepository(DB_FILE);
        service = new TaskService(repository);
    }

    @AfterEach
    void tearDown() {
        File file = new File(DB_FILE);
        if (file.exists()) file.delete();
    }

    @Test
    @DisplayName("IT-01: サービス経由でのタスク作成と一覧取得の連携")
    void testCreateAndListIntegration() {
        // 1. 作成
        service.execute(new CreateTaskCommand("ITタスク", "結合テスト用", null, Priority.HIGH));

        // 2. 取得
        List<TaskDTO> tasks = service.execute(new SearchCriteria("created_at", null, null, null));

        assertEquals(1, tasks.size());
        assertEquals("ITタスク", tasks.get(0).title());
        assertEquals("HIGH", tasks.get(0).priority());
    }

    @Test
    @DisplayName("IT-02: フィルタ条件のSQL連携（ステータス・優先度）")
    void testFilterIntegration() {
        service.execute(new CreateTaskCommand("高優先度", null, null, Priority.HIGH));
        service.execute(new CreateTaskCommand("低優先度", null, null, Priority.LOW));

        // 優先度フィルタ
        List<TaskDTO> highTasks = service.execute(new SearchCriteria(null, null, "HIGH", null));
        assertEquals(1, highTasks.size());
        assertEquals("高優先度", highTasks.get(0).title());
    }
}
