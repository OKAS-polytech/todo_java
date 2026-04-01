package com.example.todo.domain.models;

import java.util.Objects;

/**
 * グループを表すドメインエンティティ。
 */
public class Group {
    private Long id;
    private final String name;

    public Group(String name) {
        this(null, name);
    }

    public Group(Long id, String name) {
        validateName(name);
        this.id = id;
        this.name = name;
    }

    private void validateName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("グループ名は必須入力です。");
        }
    }

    // Getters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Group group = (Group) o;
        return Objects.equals(id, group.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
