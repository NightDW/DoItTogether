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

@Service
@Transactional
public class TaskServiceImpl implements TaskService {

    @Autowired
    private TaskMapper taskMapper;

    public int insertTask(Integer gid, String title, String content) {
        Task task = new Task();
        task.setPubTime(new Date());
        task.setTitle(title);
        task.setContent(content);
        task.setGroupId(gid);
        task.setHasSon(false);
        taskMapper.insertTask(task);
        return task.getId();
    }

    public void deleteTaskById(Integer id) {
        //先删除其子任务，再删除该任务
        //删除之后再看看它本来的父任务是否还有子任务，没有的话需要把它设置为非父任务
        taskMapper.deleteTaskRecordsByFatherId(id);
        Task task = taskMapper.selectTaskById(id);
        taskMapper.deleteTaskRecordById(id);
        if(task.getFatherId() == null)
            return;
        List<Task> list = taskMapper.selectTasksByFatherId(task.getFatherId());
        if(list == null || list.size() == 0){
            task = new Task(task.getFatherId());
            task.setHasSon(false);
            taskMapper.updateTaskByIdSelectively(task);
        }
    }

    public void deleteTasksByGroupId(Integer gid) {
        //先把该群的所有任务的父任务置为空，再删除这些任务
        taskMapper.clearTasksFatherIdInThisGroup(gid);
        taskMapper.deleteTaskRecordsByGroupId(gid);
    }

    public int updateTaskByIdSelectively(Task task) {
        return taskMapper.updateTaskByIdSelectively(task);
    }

    public void splitTask(Integer fatherId, Integer groupId, List<Task> sonsBaseInfo) {
        Task father = new Task();
        father.setId(fatherId);
        father.setHasSon(true);
        taskMapper.updateTaskByIdSelectively(father);
        for(Task son : sonsBaseInfo){
            son.setPubTime(new Date());
            son.setGroupId(groupId);
            son.setFatherId(fatherId);
            son.setHasSon(false);
            taskMapper.insertTask(son);
        }
    }

    public int acceptTask(Integer mid, Integer uid, Integer tid) {
        Task task = new Task();
        task.setId(tid);
        Membership membership = new Membership(mid);
        membership.setUserPubInfo(new User(uid));
        task.setAcceptorPubInfo(membership);
        return taskMapper.updateTaskByIdSelectively(task);
    }

    public void finishTask(Integer tid, String result) {
        Task task = new Task();
        task.setId(tid);
        task.setFinTime(new Date());
        task.setResult(result);
        taskMapper.updateTaskByIdSelectively(task);
        //完成之后需要检查它是否是子任务
        //如果是子任务，则需要判断它的父任务进度是否达到100%，如果是，则父任务也完成
        Task finTask = taskMapper.selectTaskById(tid);
        if(finTask.getFatherId() == null)
            return;
        Task fatherTask = taskMapper.selectTaskById(finTask.getFatherId());
        if(fatherTask.getProgress() == 1.0)
            finishTask(fatherTask.getId(), "All sons done");
    }

    public int giveUpTask(Integer tid) {
        return taskMapper.clearTaskAcceptorById(tid);
    }

    public int giveUpMyUnfinishedTaskInThisGroup(Integer mid, Integer gid) {
        return taskMapper.clearUnfinishedTasksAcceptor(mid, gid);
    }

    public int updateFinishedTaskAcceptorInThisGroup(Integer oldMid, Integer newMid, Integer newUid, Integer groupId) {
        return taskMapper.updateFinishedTaskAccIdInThisGroup(oldMid, newMid, newUid, groupId);
    }

    public Task selectTaskById(Integer id) {
        return taskMapper.selectTaskById(id);
    }

    public List<Task> selectTasksByFatherId(Integer tid) {
        return taskMapper.selectTasksByFatherId(tid);
    }

    public List<Task> selectTasksByAccUserId(Integer uid) {
        return taskMapper.selectTasksByAccUserId(uid);
    }

    public List<Task> selectRootTasksByGroupId(Integer gid) {
        return taskMapper.selectRootTasksByGroupId(gid);
    }
}
