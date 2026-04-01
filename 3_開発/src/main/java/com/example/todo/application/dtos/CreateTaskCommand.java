package com.example.todo.application.dtos;

import com.example.todo.domain.models.Priority;
import java.time.LocalDateTime;

/**
 * タスク作成のためのコマンドオブジェクト。
 */
public record CreateTaskCommand(
    Long userId,
    Long groupId,
    String title,
    String content,
    LocalDateTime dueDate,
    Priority priority
) {}
