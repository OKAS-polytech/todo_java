package com.example.todo.application.services;

import com.example.todo.application.dtos.*;
import com.example.todo.application.ports.in.*;
import com.example.todo.application.ports.out.TaskRepositoryPort;
import com.example.todo.domain.models.Task;
import java.util.List;
import java.util.stream.Collectors;

/**
 * TODOリストのアプリケーションサービス（全ポート実装版）。
 */
public class TaskService implements CreateTaskInputPort, GetTasksInputPort, UpdateTaskInputPort, DeleteTaskInputPort {

    private final TaskRepositoryPort repository;

    public TaskService(TaskRepositoryPort repository) {
        this.repository = repository;
    }

    @Override
    public void execute(CreateTaskCommand command) {
        Task task = new Task(
            command.title(),
            command.content(),
            command.dueDate(),
            command.priority()
        );
        repository.save(task);
    }

    @Override
    public List<TaskDTO> execute(SearchCriteria query) {
        return repository.findAll(query).stream()
                .map(TaskDTO::fromDomain)
                .collect(Collectors.toList());
    }

    @Override
    public void execute(UpdateTaskCommand command) {
        Task task = repository.findById(command.id());
        if (task == null) {
            throw new IllegalArgumentException("指定されたタスク(ID: " + command.id() + ")が見つかりません。");
        }
        task.updateDetails(
            command.title(),
            command.content(),
            command.dueDate(),
            command.priority(),
            command.status()
        );
        repository.save(task);
    }

    @Override
    public void delete(Long id) {
        repository.delete(id);
    }

    @Override
    public void deleteCompleted() {
        repository.deleteCompleted();
    }
}
