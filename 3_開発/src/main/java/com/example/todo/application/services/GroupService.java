package com.example.todo.application.services;

import com.example.todo.application.ports.in.GroupInputPort;
import com.example.todo.application.ports.out.GroupRepositoryPort;
import com.example.todo.domain.models.Group;
import java.util.List;

/**
 * グループ管理サービスの実装。
 */
public class GroupService implements GroupInputPort {

    private final GroupRepositoryPort groupRepository;

    public GroupService(GroupRepositoryPort groupRepository) {
        this.groupRepository = groupRepository;
    }

    @Override
    public Group createGroup(String name, Long ownerId) {
        Group group = new Group(name);
        groupRepository.save(group);
        groupRepository.addMember(group.getId(), ownerId);
        return group;
    }

    @Override
    public void joinGroup(Long groupId, Long userId) {
        Group group = groupRepository.findById(groupId);
        if (group == null) {
            throw new IllegalArgumentException("指定されたグループIDが見つかりません。");
        }
        if (groupRepository.isMember(groupId, userId)) {
            throw new IllegalArgumentException("既にこのグループに所属しています。");
        }
        groupRepository.addMember(groupId, userId);
    }

    @Override
    public List<Group> getMyGroups(Long userId) {
        return groupRepository.findByUserId(userId);
    }
}
