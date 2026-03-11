package com.example.todo.application.services;

import com.example.todo.application.dtos.CreateTaskCommand;
import com.example.todo.application.dtos.UpdateTaskCommand;
import com.example.todo.application.ports.out.TaskRepositoryPort;
import com.example.todo.domain.models.Priority;
import com.example.todo.domain.models.Status;
import com.example.todo.domain.models.Task;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * TaskServiceの更新・削除系の単体テスト。
 */
@ExtendWith(MockitoExtension.class)
class TaskServiceFullTest {

    @Mock
    private TaskRepositoryPort repository;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(repository);
    }

    @Test
    @DisplayName("UC3: タスク更新が正しく実行されること")
    void testUpdateTask() {
        Task mockTask = new Task(1L, "旧タイトル", "旧内容", null, Priority.LOW, Status.TODO, LocalDateTime.now());
        when(repository.findById(1L)).thenReturn(mockTask);

        UpdateTaskCommand command = new UpdateTaskCommand(1L, "新タイトル", "新内容", null, Priority.HIGH, Status.DONE);
        taskService.execute(command);

        assertEquals("新タイトル", mockTask.getTitle());
        assertEquals(Status.DONE, mockTask.getStatus());
        verify(repository).save(mockTask);
    }

    @Test
    @DisplayName("UC4/6: 削除・一括削除がリポジトリを呼び出すこと")
    void testDelete() {
        taskService.delete(1L);
        verify(repository).delete(1L);

        taskService.deleteCompleted();
        verify(repository).deleteCompleted();
    }
}
