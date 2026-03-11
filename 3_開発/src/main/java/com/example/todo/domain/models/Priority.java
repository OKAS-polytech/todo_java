package com.example.todo.domain.models;

/**
 * タスクの優先度を表す列挙型。
 */
public enum Priority {
    /** 高い優先度 */
    HIGH,
    /** 中程度の優先度 */
    MEDIUM,
    /** 低い優先度 */
    LOW;

    /**
     * 文字列からPriorityを取得する。
     * @param value 文字列
     * @return Priority
     */
    public static Priority fromString(String value) {
        try {
            return Priority.valueOf(value.toUpperCase());
        } catch (IllegalArgumentException e) {
            return MEDIUM;
        }
    }
}
