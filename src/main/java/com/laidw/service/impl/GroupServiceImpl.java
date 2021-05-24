package com.laidw.service.impl;

import com.laidw.entity.Group;
import com.laidw.entity.User;
import com.laidw.entity.common.Role;
import com.laidw.mapper.GroupMapper;
import com.laidw.service.GroupService;
import com.laidw.service.MembershipService;
import com.laidw.service.MessageService;
import com.laidw.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * GroupService的实现类
 */
@Service
@Transactional
public class GroupServiceImpl implements GroupService {

    @Autowired
    private GroupMapper groupMapper;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private TaskService taskService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private User systemUser;

    @Value("${dit.system-user.group-nickname}")
    private String nickname;

    @Value("${dit.img.default-img.group}")
    private String groupImg;


    public void foundGroup(Integer founderId, Group group) {

        //设置群组的默认头像
        if(group.getIconUrl() == null)
            group.setIconUrl(groupImg);

        //插入群组数据
        groupMapper.insertGroup(group);

        //创建者和系统用户加入群组，系统用户用于代表已经退群的用户
        //joinGroup()方法在将Membership插入数据库的同时会发送一条提示消息
        membershipService.joinGroup(founderId, group.getId(), Role.FOUNDER, null);
        membershipService.insertMembershipReturnId(systemUser.getId(), group.getId(), Role.NORMAL, nickname);
    }

    public void deleteGroupById(Integer id) {
        taskService.deleteAllTasksByGroupId(id);
        messageService.deleteGroupMessagesByGroupId(id);
        membershipService.deleteMembershipsByGroupId(id);
        groupMapper.deleteGroupById(id);
    }

    public int updateGroupByIdSelectively(Group group) {
        return groupMapper.updateGroupByIdSelectively(group);
    }

    public Group selectGroupById(Integer id, Boolean onlyPubInfo) {
        return onlyPubInfo ? groupMapper.selectGroupPubInfoById(id) : groupMapper.selectGroupById(id);
    }

    public List<Group> searchGroups(String key, Boolean rough) {
        return rough ? groupMapper.searchGroupsLike("%" + key + "%") : groupMapper.searchGroupsByKey(key);
    }
}
