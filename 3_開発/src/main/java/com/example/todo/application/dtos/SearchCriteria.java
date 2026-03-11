package com.example.todo.application.dtos;

/**
 * タスク検索・整理のためのクエリ属性。
 */
public record SearchCriteria(
    String sortField,
    String filterStatus,
    String filterPriority,
    String keyword
) {}
