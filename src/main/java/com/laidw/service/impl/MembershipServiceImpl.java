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

@Service
@Transactional
public class MembershipServiceImpl implements MembershipService {

    @Autowired
    private MembershipMapper membershipMapper;

    @Autowired
    private MessageService messageService;

    @Autowired
    private TaskService taskService;

    @Value("${dit.join-in-group-msg}")
    private String joinInGroupMsg;

    @Autowired
    private User systemUser;

    //这里的返回值为插入的Membership的id值
    public int insertMembership(Integer uid, Integer gid, Role role, String nickname){
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
        messageService.sendMsg(false, insertMembership(uid, gid, role, nickname), gid, joinInGroupMsg);
    }

    //退群前将消息的发送者和已完成的任务接收者改成"已退群用户"，放弃未完成的任务，然后删除该用户的Membership信息
    public void leaveGroup(Integer uid, Integer gid) {
        Membership userMembership = membershipMapper.selectMembershipPubInfo(uid, gid);
        Membership systemMembership = membershipMapper.selectMembershipPubInfo(systemUser.getId(), gid);

        messageService.updateGroupMessageSenderId(userMembership.getId(), systemMembership.getId(), gid);
        taskService.giveUpMyUnfinishedTaskInThisGroup(userMembership.getId(), gid);
        taskService.updateFinishedTaskAcceptorInThisGroup(userMembership.getId(), systemMembership.getId(), systemUser.getId(), gid);
        membershipMapper.deleteMembership(uid, gid);
    }

    public int updateMembershipSelectively(Membership membership) {
        return membershipMapper.updateMembershipSelectively(membership);
    }

    public int updateMembershipByIdSelectively(Membership membership){
        return  membershipMapper.updateMembershipByIdSelectively(membership);
    }

    public Membership selectMembership(Integer uid, Integer gid, Boolean onlyPubInfo) {
        if(onlyPubInfo)
            return membershipMapper.selectMembershipPubInfo(uid, gid);
        return membershipMapper.selectMembership(uid, gid);
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
