package com.example.todo.infrastructure.persistence;

import com.example.todo.application.dtos.SearchCriteria;
import com.example.todo.domain.models.Priority;
import com.example.todo.domain.models.Task;
import org.junit.jupiter.api.*;
import java.io.File;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SQLiteTaskRepositoryのテスト。
 * 実際にテンポラリファイルを用いてDB操作を検証する。
 */
class SQLiteTaskRepositoryTest {

    private static final String DB_FILE = "test_todo.db";
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
        Task task = new Task("SQLiteテスト", "内容", null, Priority.MEDIUM);
        repository.save(task);

        assertNotNull(task.getId());

        Task found = repository.findById(task.getId());
        assertNotNull(found);
        assertEquals("SQLiteテスト", found.getTitle());
    }

    @Test
    @DisplayName("正常系：キーワード検索が正しく動作すること")
    void testFindAllWithKeyword() {
        repository.save(new Task("Java勉強", "内容はひみつ", null, Priority.HIGH));
        repository.save(new Task("買い物", "牛乳を買う", null, Priority.LOW));

        SearchCriteria criteria = new SearchCriteria(null, null, null, "Java");
        List<Task> result = repository.findAll(criteria);

        assertEquals(1, result.size());
        assertEquals("Java勉強", result.get(0).getTitle());
    }

    @Test
    @DisplayName("正常系：完了済みタスクを一括削除できること")
    void testDeleteCompleted() {
        Task t1 = new Task("タスク1", null, null, Priority.LOW);
        t1.complete();
        repository.save(t1);

        Task t2 = new Task("タスク2", null, null, Priority.LOW);
        repository.save(t2);

        int deleted = repository.deleteCompleted();
        assertEquals(1, deleted);

        List<Task> all = repository.findAll(new SearchCriteria(null, null, null, null));
        assertEquals(1, all.size());
        assertEquals("タスク2", all.get(0).getTitle());
    }
}
