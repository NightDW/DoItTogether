package com.laidw.mapper;

import com.laidw.entity.Group;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试GroupMapper
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class GroupMapperTest {

    @Autowired
    private GroupMapper groupMapper;

    @Test
    public void insertGroupTest(){
        Group group = new Group();
        for(int i = 1; i < 10; i++){
            group.setName("sysu" + i);
            group.setSlogan("slogan" + i);
            group.setIconUrl("url" + i);
            groupMapper.insertGroup(group);
        }
    }

    @Test
    public void updateGroupByIdTest(){
        Group group = new Group();
        group.setId(9);
        group.setSlogan("hello");
        groupMapper.updateGroupByIdSelectively(group);
        group.setId(8);
        group.setName("group");
        groupMapper.updateGroupByIdSelectively(group);
    }

    @Test
    public void deleteGroupByIdTest(){
        groupMapper.deleteGroupById(8);
        groupMapper.deleteGroupById(9);
    }

    /*
     * 查询方法测试比较复杂，因此暂时忽略
     */
}
