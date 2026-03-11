package com.example.todo.application.dtos;

import com.example.todo.domain.models.Priority;
import com.example.todo.domain.models.Status;
import java.time.LocalDateTime;

/**
 * タスク更新のためのコマンドオブジェクト。
 */
public record UpdateTaskCommand(
    Long id,
    String title,
    String content,
    LocalDateTime dueDate,
    Priority priority,
    Status status
) {}
