package com.example.todo.domain.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Taskエンティティの単体テスト (ver2)。
 */
class TaskTest {

    @Test
    @DisplayName("正常系：タスクが正しく生成されること")
    void testCreateTaskSuccess() {
        LocalDateTime now = LocalDateTime.now();
        Task task = new Task(1L, 10L, "宿題", "数学のプリント", now.plusDays(1), Priority.HIGH);

        assertEquals(1L, task.getUserId());
        assertEquals(10L, task.getGroupId());
        assertEquals("宿題", task.getTitle());
        assertEquals(Priority.HIGH, task.getPriority());
    }

    @Test
    @DisplayName("異常系：タイトルが空の場合に例外が発生すること")
    void testCreateTaskEmptyTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Task(1L, null, "", "内容", LocalDateTime.now(), Priority.MEDIUM);
        });
    }

    @Test
    @DisplayName("正常系：タスクを完了状態にできること")
    void testCompleteTask() {
        Task task = new Task(1L, null, "テスト", null, null, Priority.LOW);
        task.complete();
        assertEquals(Status.DONE, task.getStatus());
    }
}
