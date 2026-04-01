package com.example.todo.application.ports.in;

import com.example.todo.domain.models.User;

/**
 * ユーザー認証ユースケースの入力ポート。
 */
public interface AuthInputPort {
    User register(String username, String password);
    User login(String username, String password);
}
