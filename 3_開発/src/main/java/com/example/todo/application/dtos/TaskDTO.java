package com.example.todo.application.dtos;

import com.example.todo.domain.models.Task;
import java.time.format.DateTimeFormatter;

/**
 * UI層に渡すためのタスクデータ転送オブジェクト。
 */
public record TaskDTO(
    Long id,
    String title,
    String content,
    String dueDate,
    String priority,
    String status,
    String createdAt
) {
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

    /**
     * DomainモデルからDTOへ変換する。
     * @param task タスクドメインモデル
     * @return TaskDTO
     */
    public static TaskDTO fromDomain(Task task) {
        return new TaskDTO(
            task.getId(),
            task.getTitle(),
            task.getContent(),
            task.getDueDate() != null ? task.getDueDate().format(formatter) : "-",
            task.getPriority().name(),
            task.getStatus().name(),
            task.getCreatedAt().format(formatter)
        );
    }
}
