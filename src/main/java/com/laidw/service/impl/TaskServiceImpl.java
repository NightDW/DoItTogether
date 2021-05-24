package com.laidw.service.impl;

import com.laidw.entity.Membership;
import com.laidw.entity.Task;
import com.laidw.entity.User;
import com.laidw.mapper.TaskMapper;
import com.laidw.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * TaskService的实现类
 */
@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskMapper taskMapper;


    public int insertLeafTaskReturnId(Integer gid, String title, String content) {
        Task task = new Task();
        task.setPubTime(new Date());
        task.setTitle(title);
        task.setContent(content);
        task.setGroupId(gid);
        task.setHasSon(false);
        taskMapper.insertTask(task);
        return task.getId();
    }

    public void deleteFullTaskById(Integer id) {
        Task task = taskMapper.selectTaskById(id);

        //如果不是叶子任务，则先删除其子任务
        if(task.getHasSon())
            task.getSons().forEach(son -> deleteFullTaskById(son.getId()));

        //然后再删除本条记录
        taskMapper.deleteTaskById(id);

        //如果本任务没有父任务，则直接结束
        if(task.getFatherId() == null)
            return;

        //否则看它的父任务是否还有子任务，没有的话需要把它设置为非父任务
        List<Task> list = taskMapper.selectTasksByFatherId(task.getFatherId());
        if(list == null || list.size() == 0){
            task = new Task(task.getFatherId());
            task.setHasSon(false);
            taskMapper.updateTaskByIdSelectively(task);
        }
    }

    public void deleteAllTasksByGroupId(Integer gid) {

        //先把该群的所有任务的父任务置为空，再统一删除这些任务
        taskMapper.clearTasksFatherIdInThisGroup(gid);
        taskMapper.deleteTasksByGroupId(gid);
    }

    public int updateTaskByIdSelectively(Task task) {
        return taskMapper.updateTaskByIdSelectively(task);
    }

    public void splitTask(Integer fatherId, Integer groupId, List<Task> sonsBaseInfo) {

        //先将被切分的任务设为父任务
        Task father = new Task();
        father.setId(fatherId);
        father.setHasSon(true);
        taskMapper.updateTaskByIdSelectively(father);

        //然后插入子任务的信息
        for(Task son : sonsBaseInfo){
            son.setPubTime(new Date());
            son.setGroupId(groupId);
            son.setFatherId(fatherId);
            son.setHasSon(false);
            taskMapper.insertTask(son);
        }
    }

    public int acceptTask(Integer mid, Integer uid, Integer tid) {
        Membership membership = new Membership(mid);
        membership.setUserPubInfo(new User(uid));

        Task task = new Task();
        task.setId(tid);
        task.setAcceptorPubInfo(membership);
        return taskMapper.updateTaskByIdSelectively(task);
    }

    public void finishTask(Integer tid, String result) {
        Task task = new Task();
        task.setId(tid);
        task.setFinTime(new Date());
        task.setResult(result);
        taskMapper.updateTaskByIdSelectively(task);

        //完成之后需要检查它是否有父任务
        //如果有，则需要判断它的父任务进度是否达到100%，如果是，则父任务也完成
        Task finTask = taskMapper.selectTaskById(tid);
        if(finTask.getFatherId() == null)
            return;
        Task fatherTask = taskMapper.selectFullTaskById(finTask.getFatherId());
        if(fatherTask.getProgress() == 1.0)
            finishTask(fatherTask.getId(), "All sons done");
    }

    public int giveUpThisUnfinishedTask(Integer tid) {
        return taskMapper.clearUnfinishedTaskAcceptorById(tid);
    }

    public int giveUpMyUnfinishedTasksInThisGroup(Integer mid, Integer gid) {
        return taskMapper.giveUpMyUnfinishedTasksInThisGroup(mid, gid);
    }

    public int transferFinishedTaskAccIdInThisGroup(Integer oldMid, Integer newMid, Integer newUid, Integer groupId) {
        return taskMapper.transferFinishedTaskAccIdInThisGroup(oldMid, newMid, newUid, groupId);
    }

    public Task selectTaskById(Integer id) {
        return taskMapper.selectTaskById(id);
    }

    public Task selectFullTaskById(Integer id) {
        return taskMapper.selectFullTaskById(id);
    }

    public List<Task> selectFullTasksByFatherId(Integer tid) {
        return taskMapper.selectFullTasksByFatherId(tid);
    }

    public List<Task> selectTasksByAccUserId(Integer uid) {
        return taskMapper.selectTasksByAccUserId(uid);
    }

    public List<Task> selectRootFullTasksByGroupId(Integer gid) {
        return taskMapper.selectRootFullTasksByGroupId(gid);
    }
}
