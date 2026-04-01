package com.example.todo.application.ports.in;

import com.example.todo.application.dtos.TaskDTO;
import java.util.List;

/**
 * グループ内タスク取得のための追加ポート。
 */
public interface GetGroupTasksInputPort {
    List<TaskDTO> execute(Long groupId, Long userId);
}
