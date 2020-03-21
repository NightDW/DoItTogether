package com.laidw.mapper;

import com.laidw.entity.Group;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class GroupMapperTest {

    @Autowired
    private GroupMapper groupMapper;

    @Test
    public void insertGroupTest(){
        Group group = new Group();
        String groupName = "sysu";
        String groupSlogan = "slogan";
        for(int i = 0; i < 10; i++){
            group.setName(groupName + i);
            group.setSlogan(groupSlogan + i);
            groupMapper.insertGroup(group);
        }
    }

    @Test
    public void updateGroupByIdSelectivelyTest(){
        Group group = new Group();
        group.setId(10);
        group.setSlogan("n_slogan");
        groupMapper.updateGroupByIdSelectively(group);
        group.setId(9);
        group.setName("n_sysu");
        groupMapper.updateGroupByIdSelectively(group);
    }

    @Test
    public void deleteGroupByIdTest(){
        groupMapper.deleteGroupById(10);
        groupMapper.deleteGroupById(9);
    }

    @Test
    public void selectGroupTest(){
        Group group1 = groupMapper.selectGroupById(1);
        Group group2 = groupMapper.selectGroupPubInfoById(2);
        group1 = group2 = null;
    }
}
