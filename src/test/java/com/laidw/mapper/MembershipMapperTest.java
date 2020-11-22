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

/**
 * 测试MembershipMapper
 */
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
        for(int i = 1; i < 8; i++){
            user.setId(i);
            group.setId(i);
            membership.setNickname("nickname" + i);
            membershipMapper.insertMembership(membership);
        }
    }

    @Test
    public void updateMembershipTest(){
        Membership membership = new Membership();
        membership.setUserPubInfo(new User(7));
        membership.setGroupPubInfo(new Group(7));
        membership.setRole(Role.FOUNDER);
        membership.setNickname("boss");
        membershipMapper.updateMembershipByUGSelectively(membership);

        membership.setId(6);
        membership.setUserPubInfo(null);
        membership.setGroupPubInfo(null);
        membershipMapper.updateMembershipByIdSelectively(membership);
    }

    @Test
    public void deleteMembershipTest(){
        membershipMapper.deleteMembershipByUG(7, 7);
        membershipMapper.deleteMembershipsByGroupId(6);
    }

    /*
     * 查询方法测试比较复杂，因此暂时忽略
     */
}
