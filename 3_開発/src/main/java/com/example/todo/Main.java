package com.example.todo;

import com.example.todo.application.services.AuthService;
import com.example.todo.application.services.GroupService;
import com.example.todo.application.services.TaskService;
import com.example.todo.infrastructure.persistence.SQLiteGroupRepository;
import com.example.todo.infrastructure.persistence.SQLiteTaskRepository;
import com.example.todo.infrastructure.persistence.SQLiteUserRepository;
import com.example.todo.infrastructure.ui.ConsoleController;

/**
 * アプリケーションのメインエントリーポイント (ver2)。
 */
public class Main {
    public static void main(String[] args) {
        String dbPath = "todo_v2.db";

        // リポジトリの初期化
        SQLiteUserRepository userRepo = new SQLiteUserRepository(dbPath);
        SQLiteGroupRepository groupRepo = new SQLiteGroupRepository(dbPath);
        SQLiteTaskRepository taskRepo = new SQLiteTaskRepository(dbPath);

        // サービスの初期化
        AuthService authService = new AuthService(userRepo);
        GroupService groupService = new GroupService(groupRepo);
        TaskService taskService = new TaskService(taskRepo, groupRepo);

        // UI開始
        ConsoleController ui = new ConsoleController(taskService, authService, groupService);
        ui.start();
    }
}
