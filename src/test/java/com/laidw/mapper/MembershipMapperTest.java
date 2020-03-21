package com.laidw.mapper;

import com.laidw.entity.Group;
import com.laidw.entity.Membership;
import com.laidw.entity.User;
import com.laidw.entity.common.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MembershipMapperTest {

    @Autowired
    private MembershipMapper membershipMapper;

    @Test
    public void insertMembershipTest(){
        Membership membership = new Membership();
        User user = new User();
        Group group = new Group();
        membership.setUserPubInfo(user);
        membership.setGroupPubInfo(group);
        membership.setRole(Role.ADMIN);
        membership.setIsMute(false);
        membership.setIsShow(true);
        membership.setIsTop(false);
        for(int i = 1; i < 9; i++){
            user.setId(i);
            group.setId(i);
            membership.setNickname("nickname" + i);
            membershipMapper.insertMembership(membership);
        }
    }

    @Test
    public void updateMembershipSelectivelyTest(){
        Membership membership = new Membership();
        membership.setUserPubInfo(new User(8));
        membership.setGroupPubInfo(new Group(8));
        membership.setRole(Role.FOUNDER);
        membership.setNickname("n_nickname");
        membershipMapper.updateMembershipSelectively(membership);
    }

    @Test
    public void deleteMembershipTest(){
        membershipMapper.deleteMembership(8, 8);
    }

    @Test
    public void selectMembershipTest(){
        Membership m1 = membershipMapper.selectMembership(1, 1);
        Membership m2 = membershipMapper.selectMembershipPubInfo(2, 2);
        List<Membership> list1 = membershipMapper.selectMembershipsByUserId(3);
        List<Membership> list2 = membershipMapper.selectMembershipsPubInfoByUserId(4);
        List<Membership> list3 = membershipMapper.selectMembershipsByGroupId(5);
        List<Membership> list4 = membershipMapper.selectMembershipsPubInfoByGroupId(6);
        m1 = m2 = null;
    }
}
