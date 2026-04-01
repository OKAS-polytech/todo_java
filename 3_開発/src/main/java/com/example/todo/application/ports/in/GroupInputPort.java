package com.example.todo.application.ports.in;

import com.example.todo.domain.models.Group;
import java.util.List;

/**
 * グループ管理ユースケースの入力ポート。
 */
public interface GroupInputPort {
    Group createGroup(String name, Long ownerId);
    void joinGroup(Long groupId, Long userId);
    List<Group> getMyGroups(Long userId);
}
