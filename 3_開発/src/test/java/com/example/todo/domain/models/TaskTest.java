package com.example.todo.domain.models;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Taskエンティティの単体テスト。
 */
class TaskTest {

    @Test
    @DisplayName("正常系：タスクが正しく生成されること")
    void testCreateTaskSuccess() {
        LocalDateTime now = LocalDateTime.now();
        Task task = new Task("宿題", "数学のプリント", now.plusDays(1), Priority.HIGH);

        assertEquals("宿題", task.getTitle());
        assertEquals("数学のプリント", task.getContent());
        assertEquals(Priority.HIGH, task.getPriority());
        assertEquals(Status.TODO, task.getStatus());
        assertNotNull(task.getCreatedAt());
    }

    @Test
    @DisplayName("異常系：タイトルが空の場合に例外が発生すること")
    void testCreateTaskEmptyTitle() {
        assertThrows(IllegalArgumentException.class, () -> {
            new Task("", "内容", LocalDateTime.now(), Priority.MEDIUM);
        });
    }

    @Test
    @DisplayName("正常系：タスクを完了状態にできること")
    void testCompleteTask() {
        Task task = new Task("テスト", null, null, Priority.LOW);
        task.complete();
        assertEquals(Status.DONE, task.getStatus());
    }

    @Test
    @DisplayName("正常系：タスクの詳細を更新できること")
    void testUpdateDetails() {
        Task task = new Task("旧タイトル", "旧内容", null, Priority.LOW);
        task.updateDetails("新タイトル", "新内容", null, Priority.HIGH, Status.DONE);

        assertEquals("新タイトル", task.getTitle());
        assertEquals("新内容", task.getContent());
        assertEquals(Priority.HIGH, task.getPriority());
        assertEquals(Status.DONE, task.getStatus());
    }
}
