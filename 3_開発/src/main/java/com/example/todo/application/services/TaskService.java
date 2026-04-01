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
import com.example.todo.application.ports.out.GroupRepositoryPort;

public class TaskService implements CreateTaskInputPort, GetTasksInputPort, UpdateTaskInputPort, DeleteTaskInputPort, GetGroupTasksInputPort {

    private final TaskRepositoryPort repository;
    private final GroupRepositoryPort groupRepository;

    public TaskService(TaskRepositoryPort repository, GroupRepositoryPort groupRepository) {
        this.repository = repository;
        this.groupRepository = groupRepository;
    }

    @Override
    public void execute(CreateTaskCommand command) {
        Task task = new Task(
            command.userId(),
            command.groupId(),
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
        // デフォルトは自分のタスクのみ削除（ログインユーザーIDが必要なため、実引数で渡すように変更）
        throw new UnsupportedOperationException("userIdを指定してください");
    }

    public void deleteCompleted(Long userId) {
        repository.deleteCompleted(userId);
    }

    @Override
    public List<TaskDTO> execute(Long groupId, Long userId) {
        // グループメンバーであることを確認
        if (!groupRepository.isMember(groupId, userId)) {
            throw new IllegalArgumentException("このグループに所属していません。");
        }
        return repository.findByGroupId(groupId).stream()
                .map(TaskDTO::fromDomain)
                .collect(Collectors.toList());
    }
}
