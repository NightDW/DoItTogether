package com.laidw.service.impl;

import com.laidw.entity.Group;
import com.laidw.entity.Membership;
import com.laidw.entity.User;
import com.laidw.entity.common.Role;
import com.laidw.mapper.MembershipMapper;
import com.laidw.service.MembershipService;
import com.laidw.service.MessageService;
import com.laidw.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * MembershipService的实现类
 */
@Service
@Transactional
public class MembershipServiceImpl implements MembershipService {

    @Autowired
    private MembershipMapper membershipMapper;

    @Autowired
    private MessageService messageService;

    @Autowired
    private TaskService taskService;

    @Value("${dit.msg.join-in-group}")
    private String joinInGroupMsg;

    @Autowired
    private User systemUser;


    public int insertMembershipReturnId(Integer uid, Integer gid, Role role, String nickname){
        Membership membership = new Membership();
        membership.setUserPubInfo(new User(uid));
        membership.setGroupPubInfo(new Group(gid));
        membership.setIsMute(false);
        membership.setIsShow(true);
        membership.setIsTop(false);
        membership.setRole(role);
        membership.setNickname(nickname);
        membershipMapper.insertMembership(membership);
        return membership.getId();
    }

    public int deleteMembershipsByGroupId(Integer gid) {
        return membershipMapper.deleteMembershipsByGroupId(gid);
    }

    public void joinGroup(Integer uid, Integer gid, Role role, String nickname) {
        int mid = insertMembershipReturnId(uid, gid, role, nickname);
        messageService.sendMsgReturnId(false, mid, gid, joinInGroupMsg);
    }

    public void leaveGroup(Integer uid, Integer gid) {
        Membership userMembership = membershipMapper.selectMembershipPubInfoByUG(uid, gid);
        Membership systemMembership = membershipMapper.selectMembershipPubInfoByUG(systemUser.getId(), gid);

        //将消息的发送者和已完成的任务接收者改成"已退群用户"
        messageService.updateGroupMessageSenderId(userMembership.getId(), systemMembership.getId(), gid);
        taskService.transferFinishedTaskAccIdInThisGroup(userMembership.getId(), systemMembership.getId(), systemUser.getId(), gid);

        //放弃未完成的任务，然后删除该用户的Membership信息
        taskService.giveUpMyUnfinishedTasksInThisGroup(userMembership.getId(), gid);
        membershipMapper.deleteMembershipByUG(uid, gid);
    }

    public int updateMembershipByUGSelectively(Membership membership) {
        return membershipMapper.updateMembershipByUGSelectively(membership);
    }

    public int updateMembershipByIdSelectively(Membership membership){
        return  membershipMapper.updateMembershipByIdSelectively(membership);
    }

    public Membership selectMembershipByUG(Integer uid, Integer gid, Boolean onlyPubInfo) {
        if(onlyPubInfo)
            return membershipMapper.selectMembershipPubInfoByUG(uid, gid);
        return membershipMapper.selectMembershipByUG(uid, gid);
    }

    public Membership selectMembershipById(Integer mid, Boolean onlyPubInfo) {
        if(onlyPubInfo)
            return membershipMapper.selectMembershipPubInfoById(mid);
        return membershipMapper.selectMembershipById(mid);
    }

    public List<Membership> selectMembershipsByUserId(Integer uid, Boolean onlyPubInfo) {
        if(onlyPubInfo)
            return membershipMapper.selectMembershipsPubInfoByUserId(uid);
        return membershipMapper.selectMembershipsByUserId(uid);
    }

    public List<Membership> selectMembershipsByGroupId(Integer gid, Boolean onlyPubInfo) {
        if(onlyPubInfo)
            return membershipMapper.selectMembershipsPubInfoByGroupId(gid);
        return membershipMapper.selectMembershipsByGroupId(gid);
    }
}
