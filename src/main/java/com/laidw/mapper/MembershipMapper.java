package com.laidw.mapper;

import com.laidw.entity.Membership;
import com.sun.org.apache.xpath.internal.operations.Bool;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.security.core.parameters.P;

import java.util.List;

/**
 * 针对Membership的增删改查操作
 */
@Mapper
public interface MembershipMapper {

    //用户加入某个群时需要调用该方法
    int insertMembership(Membership membership);

    //用户退出某个群时调用该方法
    //注意群主不能调用该方法，取而代之的是解散群组功能（可在页面判断其身份然后显示相应的按钮）
    int deleteMembership(@Param("uid") Integer uid, @Param("gid") Integer gid);

    int deleteMembershipsByGroupId(Integer gid);

    //根据用户的id和群组的id修改信息，只更新非空字段
    int updateMembershipSelectively(Membership membership);

    int updateMembershipByIdSelectively(Membership membership);

    Membership selectMembershipById(Integer id);

    Membership selectMembershipPubInfoById(Integer id);

    //根据用户的id和群组的id查询信息
    Membership selectMembership(@Param("uid") Integer uid, @Param("gid") Integer gid);

    //根据用户的id和群组的id查询信息，Membership对象中不包含隐私信息
    Membership selectMembershipPubInfo(@Param("uid") Integer uid, @Param("gid") Integer gid);

    //根据用户id查询他加入的群
    List<Membership> selectMembershipsByUserId(Integer uid);

    //根据用户id查询他加入的群，Membership对象中不包含隐私信息
    List<Membership> selectMembershipsPubInfoByUserId(Integer uid);

    //查询某个群中的所有成员
    List<Membership> selectMembershipsByGroupId(Integer gid);

    //查询某个群中的所有成员，Membership对象中不包含隐私信息
    List<Membership> selectMembershipsPubInfoByGroupId(Integer gid);
}

