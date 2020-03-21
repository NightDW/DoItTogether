package com.laidw.service;

import com.laidw.entity.Membership;
import com.laidw.entity.common.Role;

import java.util.List;

public interface MembershipService {

    //这里的返回值为插入的Membership的id值
    int insertMembership(Integer uid, Integer gid, Role role, String nickname);

    int deleteMembershipsByGroupId(Integer gid);

    void joinGroup(Integer uid, Integer gid, Role role, String nickname);

    void leaveGroup(Integer uid, Integer gid);

    int updateMembershipSelectively(Membership membership);

    int updateMembershipByIdSelectively(Membership membership);

    Membership selectMembership(Integer uid, Integer gid, Boolean onlyPubInfo);

    List<Membership> selectMembershipsByUserId(Integer uid, Boolean onlyPubInfo);

    List<Membership> selectMembershipsByGroupId(Integer gid, Boolean onlyPubInfo);
}
