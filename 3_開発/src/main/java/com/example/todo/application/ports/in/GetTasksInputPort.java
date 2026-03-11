package com.example.todo.application.ports.in;

import com.example.todo.application.dtos.SearchCriteria;
import com.example.todo.application.dtos.TaskDTO;
import java.util.List;

/**
 * タスク取得ユースケースの入力ポート。
 */
public interface GetTasksInputPort {
    /**
     * 条件に合致するタスク一覧を取得する。
     * @param query 検索・整理条件
     * @return DTOのリスト
     */
    List<TaskDTO> execute(SearchCriteria query);
}
