package com.example.todo.application.ports.in;

import com.example.todo.application.dtos.CreateTaskCommand;

/**
 * タスク作成ユースケースの入力ポート。
 */
public interface CreateTaskInputPort {
    /**
     * 新しいタスクを作成する。
     * @param command 作成コマンド
     */
    void execute(CreateTaskCommand command);
}
