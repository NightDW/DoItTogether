package com.laidw.mapper;

import com.laidw.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 针对Task的增删改查操作
 * 增删改操作都是针对单条记录而言的，相关方法名称统一用xxxTask()的格式
 * 对于查询操作：
 *    1.可以查询单条记录，相关方法名称为selectTask()的格式
 *    2.也可以查询包含子任务的完整的任务信息，相关方法名称为selectFullTask()的格式
 */
@Mapper
public interface TaskMapper {

    /**
     * 插入一条任务记录
     * @param task 插入的数据
     * @return 受影响的行数
     */
    int insertTask(Task task);

    /**
     * 删除某个任务的所有直接子任务记录，一般配合deleteTaskById()使用
     * @param fid 父任务的id
     * @return 受影响的行数
     */
    int deleteTasksByFatherId(Integer fid);

    /**
     * 根据id删除任务记录，一般先通过deleteTasksByFatherId()删除其子任务
     * @param id 任务的id
     * @return 受影响的行数
     */
    int deleteTaskById(Integer id);

    /**
     * 把某个群内所有任务的父任务都修改为空，一般配合deleteTasksByGroupId()使用
     * @param gid 群组的id
     * @return 受影响的行数
     */
    int clearTasksFatherIdInThisGroup(Integer gid);

    /**
     * 删除群组内的所有任务记录，一般需要先clearTasksFatherIdInThisGroup()
     * @param gid 群组的id
     * @return 受影响的行数
     */
    int deleteTasksByGroupId(Integer gid);

    /**
     * 根据id有选择性地更新记录
     * @param task 至少需要包含id
     * @return 受影响的行数
     */
    int updateTaskByIdSelectively(Task task);

    /**
     * 清除某个未完成任务的接收者，即某人放弃了该任务
     * @param id 任务的id
     * @return 受影响的行数
     */
    int clearUnfinishedTaskAcceptorById(Integer id);

    /**
     * 清除某个成员所有未完成的任务的接收者，一般是在用户退群时使用
     * @param mid Membership的id
     * @param gid 群组的id
     * @return 受影响的行数
     */
    int giveUpMyUnfinishedTasksInThisGroup(@Param("mid") Integer mid, @Param("gid") Integer gid);

    /**
     * 把指定群内某个人已完成的任务全部转移给另一个用户，在用户退群时会用到
     * @param oldMId 被替换的Membership的id
     * @param newMId 新的Membership的id
     * @param newUid 新的用户的id
     * @param groupId 群组的id
     * @return 受影响的行数
     */
    int transferFinishedTaskAccIdInThisGroup(@Param("omid") Integer oldMId, @Param("nmid") Integer newMId, @Param("nuid") Integer newUid, @Param("gid") Integer groupId);

    /**
     * 根据id查询记录，会查询出子任务
     * @param id 记录的id
     * @return 查询结果
     */
    Task selectFullTaskById(Integer id);

    /**
     * 根据id查询记录，不查询子任务
     * @param id 记录的id
     * @return 查询结果
     */
    Task selectTaskById(Integer id);

    /**
     * 查询某个任务的子任务，会再查询出子任务的子任务
     * @param tid 父任务的id
     * @return 查询结果
     */
    List<Task> selectFullTasksByFatherId(Integer tid);

    /**
     * 查询某个任务的子任务，不会再查询出子任务的子任务
     * @param tid 父任务的id
     * @return 查询结果
     */
    List<Task> selectTasksByFatherId(Integer tid);

    /**
     * 查询某个人接收的任务，注意这些任务肯定都是叶子任务
     * @param uid 用户的id
     * @return 查询结果
     */
    List<Task> selectTasksByAccUserId(Integer uid);

    /**
     * 查询某个群组内的根任务，会查询出子任务
     * @param gid 群组的id
     * @return 查询结果
     */
    List<Task> selectRootFullTasksByGroupId(Integer gid);
}