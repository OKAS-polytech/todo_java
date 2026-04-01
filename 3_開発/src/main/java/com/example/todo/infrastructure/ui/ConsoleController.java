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

    private final TaskService taskService;
    private final AuthInputPort authPort;
    private final GroupInputPort groupPort;
    private final Scanner scanner;

    private com.example.todo.domain.models.User currentUser;
    private SearchCriteria currentCriteria;

    public ConsoleController(TaskService taskService, AuthInputPort authPort, GroupInputPort groupPort) {
        this.taskService = taskService;
        this.authPort = authPort;
        this.groupPort = groupPort;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== TODOリストアプリケーション ver2 (CUI) ===");
        while (currentUser == null) {
            showAuthMenu();
        }

        currentCriteria = new SearchCriteria(currentUser.getId(), "created_at", null, null, null);

        while (true) {
            showMainMenu();
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

    private void showAuthMenu() {
        System.out.println("\n--- 認証メニュー ---");
        System.out.println("1. ログイン");
        System.out.println("2. ユーザー登録");
        System.out.println("9. 終了");
        System.out.print("選択: ");
        String choice = scanner.nextLine();
        try {
            switch (choice) {
                case "1" -> handleLogin();
                case "2" -> handleRegister();
                case "9" -> System.exit(0);
                default -> System.out.println("無効な選択です。");
            }
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }

    private void handleLogin() {
        System.out.print("ユーザー名: ");
        String name = scanner.nextLine();
        System.out.print("パスワード: ");
        String pass = scanner.nextLine();
        currentUser = authPort.login(name, pass);
        System.out.println("ログインしました。ようこそ " + currentUser.getUsername() + " さん");
    }

    private void handleRegister() {
        System.out.print("新規ユーザー名: ");
        String name = scanner.nextLine();
        System.out.print("パスワード: ");
        String pass = scanner.nextLine();
        authPort.register(name, pass);
        System.out.println("登録しました。ログインしてください。");
    }

    private void showMainMenu() {
        System.out.println("\n--- メインメニュー (ユーザー: " + currentUser.getUsername() + ") ---");
        System.out.println("1. 自分のTODO一覧表示");
        System.out.println("2. 新規TODO作成");
        System.out.println("3. TODO更新(完了含む)");
        System.out.println("4. TODO削除");
        System.out.println("5. 表示条件設定(ソート・検索)");
        System.out.println("6. 完了済み一括削除");
        System.out.println("7. グループ管理メニュー");
        System.out.println("8. グループTODO表示");
        System.out.println("9. ログアウト/終了");
        System.out.print("選択: ");
        String choice = scanner.nextLine();
        try {
            switch (choice) {
                case "1" -> handleList();
                case "2" -> handleCreate();
                case "3" -> handleUpdate();
                case "4" -> handleDelete();
                case "5" -> handleSetCriteria();
                case "6" -> handleBulkDelete();
                case "7" -> handleGroupMenu();
                case "8" -> handleGroupTasks();
                case "9" -> {
                    currentUser = null;
                    System.out.println("ログアウトしました。");
                }
                default -> System.out.println("無効な選択です。");
            }
        } catch (Exception e) {
            System.out.println("エラー: " + e.getMessage());
        }
    }

    private void handleList() {
        List<TaskDTO> tasks = taskService.execute(currentCriteria);
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

        System.out.print("共有先グループID (個人用は空): ");
        String gIdInput = scanner.nextLine();
        Long groupId = gIdInput.isEmpty() ? null : Long.parseLong(gIdInput);

        taskService.execute(new CreateTaskCommand(currentUser.getId(), groupId, title, content, dueDate, priority));
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

        // ver2では所有者チェックを入れるべきだが、プロトタイプのため一旦そのまま
        taskService.execute(new UpdateTaskCommand(id, title, content, dueDate, priority, status));
        System.out.println("更新しました。");
    }

    private void handleDelete() {
        System.out.print("削除するタスクID: ");
        Long id = Long.parseLong(scanner.nextLine());
        taskService.delete(id);
        System.out.println("削除しました。");
    }

    private void handleGroupMenu() {
        System.out.println("\n--- グループ管理 ---");
        System.out.println("1. グループ作成");
        System.out.println("2. グループ参加");
        System.out.println("3. 所属グループ一覧");
        String choice = scanner.nextLine();
        switch (choice) {
            case "1" -> {
                System.out.print("グループ名: ");
                String name = scanner.nextLine();
                groupPort.createGroup(name, currentUser.getId());
                System.out.println("グループを作成しました。");
            }
            case "2" -> {
                System.out.print("参加するグループID: ");
                Long id = Long.parseLong(scanner.nextLine());
                groupPort.joinGroup(id, currentUser.getId());
                System.out.println("参加しました。");
            }
            case "3" -> {
                List<com.example.todo.domain.models.Group> groups = groupPort.getMyGroups(currentUser.getId());
                System.out.println("[所属グループ]");
                for (com.example.todo.domain.models.Group g : groups) {
                    System.out.println("ID: " + g.getId() + " | 名称: " + g.getName());
                }
            }
        }
    }

    private void handleGroupTasks() {
        System.out.print("表示するグループID: ");
        Long groupId = Long.parseLong(scanner.nextLine());
        List<TaskDTO> tasks = taskService.execute(groupId, currentUser.getId());
        System.out.println("\n[グループ内TODO一覧] (GroupID: " + groupId + ")");
        System.out.println("----------------------------------------------------------------------------------------------------");
        for (TaskDTO t : tasks) {
            System.out.printf("%-3d | %-15s | %-8s | %-6s\n", t.id(), t.title(), t.status(), t.createdAt().substring(5, 10));
        }
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
                currentCriteria = new SearchCriteria(currentUser.getId(), scanner.nextLine(), currentCriteria.filterStatus(), currentCriteria.filterPriority(), currentCriteria.keyword());
            }
            case "2" -> {
                System.out.print("状態: ");
                String status = scanner.nextLine();
                currentCriteria = new SearchCriteria(currentUser.getId(), currentCriteria.sortField(), status.isEmpty() ? null : status.toUpperCase(), currentCriteria.filterPriority(), currentCriteria.keyword());
            }
            case "3" -> {
                System.out.print("優先度: ");
                String priority = scanner.nextLine();
                currentCriteria = new SearchCriteria(currentUser.getId(), currentCriteria.sortField(), currentCriteria.filterStatus(), priority.isEmpty() ? null : priority.toUpperCase(), currentCriteria.keyword());
            }
            case "4" -> {
                System.out.print("キーワード: ");
                currentCriteria = new SearchCriteria(currentUser.getId(), currentCriteria.sortField(), currentCriteria.filterStatus(), currentCriteria.filterPriority(), scanner.nextLine());
            }
        }
    }

    private void handleBulkDelete() {
        System.out.print("あなたの完了済みタスクをすべて削除しますか？ (y/n): ");
        if (scanner.nextLine().equalsIgnoreCase("y")) {
            taskService.deleteCompleted(currentUser.getId());
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
