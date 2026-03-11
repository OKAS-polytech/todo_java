package com.example.todo.infrastructure.ui;

import com.example.todo.application.dtos.*;
import com.example.todo.application.ports.in.*;
import com.example.todo.application.services.TaskService;
import com.example.todo.domain.models.Priority;
import com.example.todo.domain.models.Status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;

/**
 * コンソールベースのユーザーインターフェース。
 */
public class ConsoleController {

    private final CreateTaskInputPort createPort;
    private final GetTasksInputPort getPort;
    private final UpdateTaskInputPort updatePort;
    private final DeleteTaskInputPort deletePort;
    private final Scanner scanner;

    private SearchCriteria currentCriteria = new SearchCriteria("created_at", null, null, null);

    public ConsoleController(TaskService service) {
        this.createPort = service;
        this.getPort = service;
        this.updatePort = service;
        this.deletePort = service;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== TODOリストアプリケーション (CUIプロトタイプ) ===");
        while (true) {
            showMenu();
            String choice = scanner.nextLine();
            try {
                switch (choice) {
                    case "1" -> handleList();
                    case "2" -> handleCreate();
                    case "3" -> handleUpdate();
                    case "4" -> handleDelete();
                    case "5" -> handleSetCriteria();
                    case "6" -> handleBulkDelete();
                    case "9" -> {
                        System.out.println("終了します。");
                        return;
                    }
                    default -> System.out.println("無効な選択です。");
                }
            } catch (Exception e) {
                System.out.println("エラー: " + e.getMessage());
            }
        }
    }

    private void showMenu() {
        System.out.println("\n--- メニュー ---");
        System.out.println("1. 一覧表示");
        System.out.println("2. 新規作成");
        System.out.println("3. タスク更新(完了含む)");
        System.out.println("4. タスク削除");
        System.out.println("5. 表示条件設定(ソート・検索)");
        System.out.println("6. 完了済み一括削除");
        System.out.println("9. 終了");
        System.out.print("選択: ");
    }

    private void handleList() {
        List<TaskDTO> tasks = getPort.execute(currentCriteria);
        System.out.println("\n[TODO一覧] (ソート: " + currentCriteria.sortField() + ", 状態F: " + currentCriteria.filterStatus() + ", 優先度F: " + currentCriteria.filterPriority() + ", 検索: " + currentCriteria.keyword() + ")");
        System.out.println("----------------------------------------------------------------------------------------------------");
        System.out.printf("%-3s | %-15s | %-20s | %-10s | %-8s | %-6s\n", "ID", "タイトル", "期限", "優先度", "状態", "作成日");
        System.out.println("----------------------------------------------------------------------------------------------------");
        for (TaskDTO t : tasks) {
            System.out.printf("%-3d | %-15s | %-20s | %-10s | %-8s | %-6s\n",
                t.id(), truncate(t.title(), 15), t.dueDate(), t.priority(), t.status(), t.createdAt().substring(5, 10));
        }
    }

    private void handleCreate() {
        System.out.print("タイトル: ");
        String title = scanner.nextLine();
        System.out.print("内容: ");
        String content = scanner.nextLine();
        System.out.print("期限 (yyyy-MM-dd HH:mm) [省略可]: ");
        LocalDateTime dueDate = parseDateTime(scanner.nextLine());
        System.out.print("優先度 (HIGH/MEDIUM/LOW) [デフォルト:MEDIUM]: ");
        Priority priority = Priority.fromString(scanner.nextLine());

        createPort.execute(new CreateTaskCommand(title, content, dueDate, priority));
        System.out.println("作成しました。");
    }

    private void handleUpdate() {
        System.out.print("更新するタスクID: ");
        Long id = Long.parseLong(scanner.nextLine());

        // 簡易化のため全項目再入力（プロトタイプ仕様）
        System.out.print("新しいタイトル: ");
        String title = scanner.nextLine();
        System.out.print("新しい内容: ");
        String content = scanner.nextLine();
        System.out.print("新しい期限 (yyyy-MM-dd HH:mm): ");
        LocalDateTime dueDate = parseDateTime(scanner.nextLine());
        System.out.print("新しい優先度 (HIGH/MEDIUM/LOW): ");
        Priority priority = Priority.fromString(scanner.nextLine());
        System.out.print("新しい状態 (TODO/DONE): ");
        Status status = Status.valueOf(scanner.nextLine().toUpperCase());

        updatePort.execute(new UpdateTaskCommand(id, title, content, dueDate, priority, status));
        System.out.println("更新しました。");
    }

    private void handleDelete() {
        System.out.print("削除するタスクID: ");
        Long id = Long.parseLong(scanner.nextLine());
        deletePort.delete(id);
        System.out.println("削除しました。");
    }

    private void handleSetCriteria() {
        System.out.println("1. ソート変更 (due_date / priority / created_at)");
        System.out.println("2. 状態フィルタ (TODO / DONE / クリア:空)") ;
        System.out.println("3. 優先度フィルタ (HIGH / MEDIUM / LOW / クリア:空)") ;
        System.out.println("4. キーワード検索 (タイトル・内容)");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1" -> {
                System.out.print("フィールド名: ");
                currentCriteria = new SearchCriteria(scanner.nextLine(), currentCriteria.filterStatus(), currentCriteria.filterPriority(), currentCriteria.keyword());
            }
            case "2" -> {
                System.out.print("状態: ");
                String status = scanner.nextLine();
                currentCriteria = new SearchCriteria(currentCriteria.sortField(), status.isEmpty() ? null : status.toUpperCase(), currentCriteria.filterPriority(), currentCriteria.keyword());
            }
            case "3" -> {
                System.out.print("優先度: ");
                String priority = scanner.nextLine();
                currentCriteria = new SearchCriteria(currentCriteria.sortField(), currentCriteria.filterStatus(), priority.isEmpty() ? null : priority.toUpperCase(), currentCriteria.keyword());
            }
            case "4" -> {
                System.out.print("キーワード: ");
                currentCriteria = new SearchCriteria(currentCriteria.sortField(), currentCriteria.filterStatus(), currentCriteria.filterPriority(), scanner.nextLine());
            }
        }
    }

    private void handleBulkDelete() {
        System.out.print("完了済みのタスクをすべて削除しますか？ (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            deletePort.deleteCompleted();
            System.out.println("一括削除しました。");
        }
    }

    private LocalDateTime parseDateTime(String input) {
        if (input == null || input.trim().isEmpty()) return null;
        try {
            return LocalDateTime.parse(input, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        } catch (DateTimeParseException e) {
            System.out.println("日付形式が正しくありません。無効な値として扱います。");
            return null;
        }
    }

    private String truncate(String s, int n) {
        if (s == null) return "";
        return s.length() > n ? s.substring(0, n - 3) + "..." : s;
    }
}
