package com.example.todo.application.ports.in;

/**
 * タスク削除ユースケースの入力ポート。
 */
public interface DeleteTaskInputPort {
    /**
     * 指定されたIDのタスクを削除する。
     * @param id タスクID
     */
    void delete(Long id);

    /**
     * 完了済みのタスクをすべて削除する。
     */
    void deleteCompleted();
}
