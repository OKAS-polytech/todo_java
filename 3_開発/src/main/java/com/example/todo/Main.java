package com.example.todo;

import com.example.todo.application.services.TaskService;
import com.example.todo.infrastructure.persistence.SQLiteTaskRepository;
import com.example.todo.infrastructure.ui.ConsoleController;

/**
 * アプリケーションのメインエントリーポイント。
 */
public class Main {
    public static void main(String[] args) {
        // 依存関係の注入（手動）
        SQLiteTaskRepository repository = new SQLiteTaskRepository("todo.db");
        TaskService service = new TaskService(repository);
        ConsoleController ui = new ConsoleController(service);

        // UI開始
        ui.start();
    }
}
