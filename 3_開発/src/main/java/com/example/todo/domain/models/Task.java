package com.example.todo.domain.models;

import java.time.LocalDateTime;

/**
 * TODOリストのタスクを表すドメインエンティティ。
 * ビジネスロジックと不変条件の維持を担当する。
 */
public class Task {
    private Long id;
    private Long userId;
    private Long groupId;
    private String title;
    private String content;
    private LocalDateTime dueDate;
    private Priority priority;
    private Status status;
    private LocalDateTime createdAt;

    /**
     * 新規タスク作成用のコンストラクタ (ver2: userId, groupId を追加)
     */
    public Task(Long userId, Long groupId, String title, String content, LocalDateTime dueDate, Priority priority) {
        validateTitle(title);
        this.userId = userId;
        this.groupId = groupId;
        this.title = title;
        this.content = content;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = Status.TODO;
        this.createdAt = LocalDateTime.now();
    }

    /**
     * 全属性を指定するコンストラクタ（再構築用）。
     */
    public Task(Long id, Long userId, Long groupId, String title, String content, LocalDateTime dueDate, Priority priority, Status status, LocalDateTime createdAt) {
        this.id = id;
        this.userId = userId;
        this.groupId = groupId;
        this.title = title;
        this.content = content;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
        this.createdAt = createdAt;
    }

    /**
     * タスクを完了状態にする。
     */
    public void complete() {
        this.status = Status.DONE;
    }

    /**
     * タスクの詳細情報を更新する。
     * @param title 新しいタイトル
     * @param content 新しい内容
     * @param dueDate 新しい期限
     * @param priority 新しい優先度
     * @param status 新しいステータス
     */
    public void updateDetails(String title, String content, LocalDateTime dueDate, Priority priority, Status status) {
        validateTitle(title);
        this.title = title;
        this.content = content;
        this.dueDate = dueDate;
        this.priority = priority;
        this.status = status;
    }

    private void validateTitle(String title) {
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("タイトルは必須入力です。");
        }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getUserId() { return userId; }
    public Long getGroupId() { return groupId; }
    public String getTitle() { return title; }
    public String getContent() { return content; }
    public LocalDateTime getDueDate() { return dueDate; }
    public Priority getPriority() { return priority; }
    public Status getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
