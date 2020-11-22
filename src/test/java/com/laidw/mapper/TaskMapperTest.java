package com.laidw.mapper;

import com.laidw.entity.Membership;
import com.laidw.entity.Task;
import com.laidw.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;

/**
 * 测试TaskMapper
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class TaskMapperTest {

    @Autowired
    private TaskMapper taskMapper;

    @Test
    public void insertTaskTest(){
        insertTaskTree(1);
        insertTaskTree(2);
    }

    @Test
    public void updateTaskTest() throws Exception {
        Membership membership = new Membership(1);
        User user = new User(1);
        membership.setUserPubInfo(user);

        Task task = new Task();
        task.setAcceptorPubInfo(membership);

        //4号和5号的task都是1群里的，1号用户在1群里的MembershipId也为1
        //因此这部分相当于1号用户接收了4号和5号任务
        task.setId(4);
        taskMapper.updateTaskByIdSelectively(task);
        task.setId(5);
        taskMapper.updateTaskByIdSelectively(task);

        Thread.sleep(10000);

        //这一步相当于1号用户完成了5号任务
        task = new Task();
        task.setId(5);
        task.setFinTime(new Date());
        task.setResult("well done!");
        taskMapper.updateTaskByIdSelectively(task);

        Thread.sleep(10000);

        //这一步相当于1号用户放弃的4号任务
        taskMapper.giveUpMyUnfinishedTasksInThisGroup(1, 1);

        /*
         * 其它更新方法暂时不测试
         */
    }

    @Test
    public void deleteTaskTest() throws Exception{
        taskMapper.deleteTaskById(3);
        Thread.sleep(10000);
        taskMapper.deleteTasksByFatherId(1);
        Thread.sleep(10000);
        taskMapper.clearTasksFatherIdInThisGroup(2);
        taskMapper.deleteTasksByGroupId(2);
    }

    private void insertTaskTree(int groupId){
        Task father = new Task();
        father.setPubTime(new Date());
        father.setTitle("father");
        father.setContent("father_content");
        father.setGroupId(groupId);
        father.setHasSon(true);
        taskMapper.insertTask(father);

        Task son = new Task();
        son.setPubTime(new Date());
        for(int i = 1; i < 5; i++){
            son.setTitle("son" + i);
            son.setContent("son" + i + "_content");
            son.setGroupId(father.getGroupId());
            son.setHasSon(false);
            son.setFatherId(father.getId());
            taskMapper.insertTask(son);
        }
    }

    /*
     * 查询方法测试比较复杂，因此暂时忽略
     */
}
