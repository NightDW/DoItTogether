package com.laidw.service;

import com.laidw.entity.Task;

import java.util.List;

public interface TaskService {

    //返回插入的记录的id
    int insertTask(Integer gid, String title, String content);

    void deleteTaskById(Integer id);

    void deleteTasksByGroupId(Integer gid);

    int updateTaskByIdSelectively(Task task);

    //这里的son对象只需要提供title和content
    void splitTask(Integer fatherId, Integer groupId, List<Task> sonsBaseInfo);

    int acceptTask(Integer mid, Integer uid, Integer tid);

    void finishTask(Integer tid, String result);

    int giveUpTask(Integer tid);

    int giveUpMyUnfinishedTaskInThisGroup(Integer mid, Integer gid);

    int updateFinishedTaskAcceptorInThisGroup(Integer oldMid, Integer newMid, Integer newUid, Integer groupId);

    Task selectTaskById(Integer id);

    List<Task> selectTasksByFatherId(Integer tid);

    List<Task> selectTasksByAccUserId(Integer uid);

    List<Task> selectRootTasksByGroupId(Integer gid);
}
