package com.example.todo.application.ports.out;

import com.example.todo.domain.models.Group;
import java.util.List;

/**
 * グループリポジトリの出力ポート。
 */
public interface GroupRepositoryPort {
    void save(Group group);
    void addMember(Long groupId, Long userId);
    List<Group> findByUserId(Long userId);
    Group findById(Long id);
    boolean isMember(Long groupId, Long userId);
}
