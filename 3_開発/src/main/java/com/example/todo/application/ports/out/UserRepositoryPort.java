package com.example.todo.application.ports.out;

import com.example.todo.domain.models.User;

/**
 * ユーザーリポジトリの出力ポート。
 */
public interface UserRepositoryPort {
    void save(User user);
    User findByUsername(String username);
    User findById(Long id);
}
