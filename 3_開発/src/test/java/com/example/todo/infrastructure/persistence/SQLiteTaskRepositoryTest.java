package com.example.todo.infrastructure.persistence;

import com.example.todo.application.dtos.SearchCriteria;
import com.example.todo.domain.models.Priority;
import com.example.todo.domain.models.Task;
import org.junit.jupiter.api.*;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SQLiteTaskRepositoryのテスト (ver2)。
 */
class SQLiteTaskRepositoryTest {

    private static final String DB_FILE = "test_todo_v2.db";
    private SQLiteTaskRepository repository;

    @BeforeEach
    void setUp() {
        repository = new SQLiteTaskRepository(DB_FILE);
    }

    @AfterEach
    void tearDown() {
        File file = new File(DB_FILE);
        if (file.exists()) {
            file.delete();
        }
    }

    @Test
    @DisplayName("正常系：タスクの保存とIDによる取得ができること")
    void testSaveAndFindById() {
        Task task = new Task(1L, null, "SQLiteテスト", "内容", null, Priority.MEDIUM);
        repository.save(task);

        assertNotNull(task.getId());

        Task found = repository.findById(task.getId());
        assertNotNull(found);
        assertEquals(1L, found.getUserId());
        assertEquals("SQLiteテスト", found.getTitle());
    }

    @Test
    @DisplayName("正常系：キーワード検索が正しく動作すること")
    void testFindAllWithKeyword() {
        repository.save(new Task(1L, null, "Java勉強", "内容はひみつ", null, Priority.HIGH));
        repository.save(new Task(1L, null, "買い物", "牛乳を買う", null, Priority.LOW));

        SearchCriteria criteria = new SearchCriteria(1L, null, null, null, "Java");
        List<Task> result = repository.findAll(criteria);

        assertEquals(1, result.size());
        assertEquals("Java勉強", result.get(0).getTitle());
    }

    @Test
    @DisplayName("正常系：完了済みタスクを一括削除できること")
    void testDeleteCompleted() {
        Task t1 = new Task(1L, null, "タスク1", null, null, Priority.LOW);
        t1.complete();
        repository.save(t1);

        Task t2 = new Task(1L, null, "タスク2", null, null, Priority.LOW);
        repository.save(t2);

        int deleted = repository.deleteCompleted(1L);
        assertEquals(1, deleted);

        List<Task> all = repository.findAll(new SearchCriteria(1L, null, null, null, null));
        assertEquals(1, all.size());
        assertEquals("タスク2", all.get(0).getTitle());
    }
}
