package com.example.todo.domain.models;

import java.util.Objects;

/**
 * ユーザーを表すドメインエンティティ。
 */
public class User {
    private Long id;
    private final String username;
    private final String passwordHash;

    public User(String username, String passwordHash) {
        this(null, username, passwordHash);
    }

    public User(Long id, String username, String passwordHash) {
        validateUsername(username);
        this.id = id;
        this.username = username;
        this.passwordHash = passwordHash;
    }

    private void validateUsername(String username) {
        if (username == null || username.trim().isEmpty()) {
            throw new IllegalArgumentException("ユーザー名は必須入力です。");
        }
    }

    // Getters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
