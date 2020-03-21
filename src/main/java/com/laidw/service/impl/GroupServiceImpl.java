package com.laidw.service.impl;

import com.laidw.entity.Group;
import com.laidw.entity.Membership;
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

    @Value("${dit.system-user.nickname}")
    private String nickname;

    @Value("${dit.default-group-img}")
    private String groupImg;

    //群建立后，创建者自动加入该群，同时系统用户也悄悄加入
    public void foundGroup(Integer founderId, Group group) {
        if(group.getIconUrl() == null)
            group.setIconUrl(groupImg);
        groupMapper.insertGroup(group);
        membershipService.joinGroup(founderId, group.getId(), Role.FOUNDER, null);
        //昵称如果硬编码到这里会有乱码
        membershipService.insertMembership(systemUser.getId(), group.getId(), Role.NORMAL, nickname);
    }

    public void deleteGroupById(Integer id) {
        taskService.deleteTasksByGroupId(id);
        messageService.deleteMessagesByGroupId(id);
        membershipService.deleteMembershipsByGroupId(id);
        groupMapper.deleteGroupById(id);
    }

    public int updateGroupByIdSelectively(Group group) {
        return groupMapper.updateGroupByIdSelectively(group);
    }

    public Group selectGroupById(Integer id, Boolean onlyPubInfo) {
        if(onlyPubInfo)
            return groupMapper.selectGroupPubInfoById(id);
        return groupMapper.selectGroupById(id);
    }

    public List<Group> searchGroups(String key, Boolean rough) {
        if(rough)
            return groupMapper.searchGroupsLike("%" + key + "%");
        return groupMapper.searchGroupsByKey(key);
    }
}
