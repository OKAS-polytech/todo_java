package com.example.todo.application.services;

import com.example.todo.application.ports.in.AuthInputPort;
import com.example.todo.application.ports.out.UserRepositoryPort;
import com.example.todo.domain.models.User;

/**
 * ユーザー認証サービスの実装。
 */
public class AuthService implements AuthInputPort {

    private final UserRepositoryPort userRepository;

    public AuthService(UserRepositoryPort userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public User register(String username, String password) {
        if (userRepository.findByUsername(username) != null) {
            throw new IllegalArgumentException("ユーザー名が既に存在します。");
        }
        // プロトタイプのため簡易的なハッシュ（実際はBCrypt等を使用すべき）
        String hash = Integer.toHexString(password.hashCode());
        User user = new User(username, hash);
        userRepository.save(user);
        return user;
    }

    @Override
    public User login(String username, String password) {
        User user = userRepository.findByUsername(username);
        if (user == null) {
            throw new IllegalArgumentException("ユーザーが見つかりません。");
        }
        String hash = Integer.toHexString(password.hashCode());
        if (!user.getPasswordHash().equals(hash)) {
            throw new IllegalArgumentException("パスワードが正しくありません。");
        }
        return user;
    }
}
