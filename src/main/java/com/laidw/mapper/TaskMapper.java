package com.laidw.mapper;

import com.laidw.entity.Task;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 针对Task的增删改查操作
 */
@Mapper
public interface TaskMapper {

    //插入一条数据
    int insertTask(Task task);

    //根据id删除任务记录
    int deleteTaskRecordById(Integer id);

    int deleteTaskRecordsByGroupId(Integer gid);

    int deleteTaskRecordsByFatherId(Integer fid);

    //根据id有选择性地更新记录
    int updateTaskByIdSelectively(Task task);

    //清除指定任务的接收者
    int clearTaskAcceptorById(Integer id);

    //某人放弃所有未完成的任务
    int clearUnfinishedTasksAcceptor(@Param("mid") Integer mid, @Param("gid") Integer gid);

    //把指定群内某个人接收的任务全部转移给另一个用户，在用户退群时会用到
    int updateFinishedTaskAccIdInThisGroup(@Param("omid") Integer oldMId, @Param("nmid") Integer newMId, @Param("nuid") Integer newUid, @Param("gid") Integer groupId);

    //把某个群内所有任务的父任务都修改为空
    int clearTasksFatherIdInThisGroup(Integer gid);

    //根据id查询记录
    Task selectTaskById(Integer id);

    //根据id查询记录，不级联查询
    Task selectTaskBaseInfoById(Integer id);

    //查询某个任务的子任务
    List<Task> selectTasksByFatherId(Integer tid);

    //查询某个人接收的任务
    List<Task> selectTasksByAccUserId(Integer uid);

    //查询某个群组内的根任务
    List<Task> selectRootTasksByGroupId(Integer gid);
}
