package com.laidw.service;

import com.laidw.entity.Task;

import java.util.List;

/**
 * 主要负责Task相关的业务，部分方法的作用可以参考TaskMapper
 */
public interface TaskService {

    /**
     * 插入一个叶子任务
     * @param gid 所在群组的id
     * @param title 任务标题
     * @param content 任务内容
     * @return 插入后返回的任务id
     */
    int insertLeafTaskReturnId(Integer gid, String title, String content);

    /**
     * 删除整棵任务树
     * @param id 根任务的id
     */
    void deleteFullTaskById(Integer id);

    /**
     * 删除群组中的所有任务
     * @param gid 群组的id
     */
    void deleteAllTasksByGroupId(Integer gid);

    /**
     * 有选择性地更新数据
     * @param task 用户提交的数据
     * @return 受影响的行数
     */
    int updateTaskByIdSelectively(Task task);

    /**
     * 切分任务
     * @param fatherId 被切分的任务的id
     * @param groupId 该任务所在群组
     * @param sonsBaseInfo 子任务的信息
     */
    void splitTask(Integer fatherId, Integer groupId, List<Task> sonsBaseInfo);

    /**
     * 接收任务
     * @param mid 接收者的MembershipId
     * @param uid 接收者的UserId
     * @param tid 任务的id
     * @return 受影响的行数
     */
    int acceptTask(Integer mid, Integer uid, Integer tid);

    /**
     * 完成任务，可用于完成叶子任务或非叶子任务
     * @param tid 任务的id
     * @param result 任务的执行结果
     */
    void finishTask(Integer tid, String result);

    /**
     * 放弃未完成的任务
     * @param tid 任务的id
     * @return 受影响的行数
     */
    int giveUpThisUnfinishedTask(Integer tid);

    /**
     * 放弃在群里的所有未完成的任务
     * @param mid 用户的MembershipId
     * @param gid 群组的id
     * @return 受影响的行数
     */
    int giveUpMyUnfinishedTasksInThisGroup(Integer mid, Integer gid);

    /**
     * 将某人已完成的任务全部转让给另一个成员
     * @param oldMid 用户的MembershipId
     * @param newMid 新用户的MembershipId
     * @param newUid 新用户的UserId
     * @param groupId 群组的id
     * @return 受影响的行数
     */
    int transferFinishedTaskAccIdInThisGroup(Integer oldMid, Integer newMid, Integer newUid, Integer groupId);

    Task selectTaskById(Integer id);

    Task selectFullTaskById(Integer id);

    List<Task> selectFullTasksByFatherId(Integer tid);

    List<Task> selectTasksByAccUserId(Integer uid);

    List<Task> selectRootFullTasksByGroupId(Integer gid);
}
