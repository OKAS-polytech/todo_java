package com.example.todo.application.ports.in;

import com.example.todo.application.dtos.UpdateTaskCommand;

/**
 * タスク更新ユースケースの入力ポート。
 */
public interface UpdateTaskInputPort {
    /**
     * 既存のタスクを更新する。
     * @param command 更新コマンド
     */
    void execute(UpdateTaskCommand command);
}
