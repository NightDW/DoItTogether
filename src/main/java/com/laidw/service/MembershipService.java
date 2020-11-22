package com.laidw.service;

import com.laidw.entity.Membership;
import com.laidw.entity.common.Role;

import java.util.List;

/**
 * 主要负责Membership相关的业务，部分方法的作用可以参考MembershipMapper
 */
public interface MembershipService {

    int insertMembershipReturnId(Integer uid, Integer gid, Role role, String nickname);

    int deleteMembershipsByGroupId(Integer gid);

    void joinGroup(Integer uid, Integer gid, Role role, String nickname);

    void leaveGroup(Integer uid, Integer gid);

    int updateMembershipByUGSelectively(Membership membership);

    int updateMembershipByIdSelectively(Membership membership);

    Membership selectMembershipByUG(Integer uid, Integer gid, Boolean onlyPubInfo);

    Membership selectMembershipById(Integer mid, Boolean onlyPubInfo);

    List<Membership> selectMembershipsByUserId(Integer uid, Boolean onlyPubInfo);

    List<Membership> selectMembershipsByGroupId(Integer gid, Boolean onlyPubInfo);
}
