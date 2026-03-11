package com.example.todo.application.ports.out;

import com.example.todo.application.dtos.SearchCriteria;
import com.example.todo.domain.models.Task;
import java.util.List;

/**
 * タスクリポジトリの出力ポート。
 */
public interface TaskRepositoryPort {
    void save(Task task);
    Task findById(Long id);
    List<Task> findAll(SearchCriteria criteria);
    void delete(Long id);
    int deleteCompleted();
}
