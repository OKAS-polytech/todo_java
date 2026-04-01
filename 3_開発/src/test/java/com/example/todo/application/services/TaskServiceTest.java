package com.example.todo.application.services;

import com.example.todo.application.dtos.CreateTaskCommand;
import com.example.todo.application.dtos.SearchCriteria;
import com.example.todo.application.dtos.TaskDTO;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * TaskServiceの単体テスト。リポジトリはモック化する。
 */
import com.example.todo.application.ports.out.GroupRepositoryPort;

@ExtendWith(MockitoExtension.class)
class TaskServiceTest {

    @Mock
    private TaskRepositoryPort repository;
    @Mock
    private GroupRepositoryPort groupRepository;

    private TaskService taskService;

    @BeforeEach
    void setUp() {
        taskService = new TaskService(repository, groupRepository);
    }

    @Test
    @DisplayName("UC1: TODO作成が正しく実行され、リポジトリの保存メソッドが呼ばれること")
    void testCreateTask() {
        CreateTaskCommand command = new CreateTaskCommand(1L, null, "テスト", "内容", null, Priority.MEDIUM);

        taskService.execute(command);

        // リポジトリのsaveが1回呼ばれたことを検証
        verify(repository, times(1)).save(any(Task.class));
    }

    @Test
    @DisplayName("UC2: TODO一覧取得が正しく実行され、DTOのリストが返ること")
    void testGetTasks() {
        Task mockTask = new Task(1L, 1L, null, "タイトル", "内容", null, Priority.LOW, Status.TODO, LocalDateTime.now());
        when(repository.findAll(any(SearchCriteria.class))).thenReturn(List.of(mockTask));

        SearchCriteria criteria = new SearchCriteria(1L, null, null, null, null);
        List<TaskDTO> result = taskService.execute(criteria);

        assertEquals(1, result.size());
        assertEquals("タイトル", result.get(0).title());
        verify(repository, times(1)).findAll(criteria);
    }
}
