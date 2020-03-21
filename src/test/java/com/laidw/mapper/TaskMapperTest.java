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
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class TaskMapperTest {

    @Autowired
    private TaskMapper taskMapper;

    @Test
    public void insertTaskTest(){
        Task father = new Task();
        father.setPubTime(new Date());
        father.setTitle("father");
        father.setContent("father_content");
        father.setGroupId(1);
        father.setHasSon(true);
        taskMapper.insertTask(father);

        Task son = new Task();
        son.setPubTime(new Date());
        for(int i = 0; i < 4; i++){
            son.setTitle("son" + i);
            son.setContent("son" + i + "_content");
            son.setGroupId(1);
            son.setHasSon(false);
            son.setFatherId(father.getId());
            taskMapper.insertTask(son);
        }
    }

    @Test
    public void updateTaskByIdSelectivelyTest(){
        Task task = new Task();
        Membership membership = new Membership();
        User user = new User();
        membership.setUserPubInfo(user);
        task.setAcceptorPubInfo(membership);

        task.setResult("finish");
        task.setFinTime(new Date());

        task.setId(5);
        taskMapper.updateTaskByIdSelectively(task);

        task.setId(4);
        user.setId(1);
        taskMapper.updateTaskByIdSelectively(task);
    }

//    @Test
//    public void deleteTaskByIdTest(){
//        taskMapper.deleteTaskById(5);
//    }

    @Test
    public void selectTaskTest(){
        Task task = taskMapper.selectTaskById(1);
        List<Task> list1 = taskMapper.selectTasksByFatherId(1);
        List<Task> list2 = taskMapper.selectRootTasksByGroupId(1);
        task = null;
    }
}
