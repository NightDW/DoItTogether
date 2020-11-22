package com.laidw.mapper;

import com.laidw.entity.Membership;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 针对Membership的增删改查操作
 */
@Mapper
public interface MembershipMapper {

    /**
     * 插入方法
     * @param membership 插入的数据
     * @return 受影响的行数
     */
    int insertMembership(Membership membership);

    /**
     * 用户退出某个群时调用该方法
     * 注意群主不能调用该方法，而应该调用解散群组功能（可在页面判断其身份然后显示相应的按钮）
     * @param uid 用户的id
     * @param gid 群组的id
     * @return 受影响的行数
     */
    int deleteMembershipByUG(@Param("uid") Integer uid, @Param("gid") Integer gid);

    /**
     * 删除群里的所有成员，该方法基本不用
     * @param gid 用户的id
     * @return 受影响的行数
     */
    int deleteMembershipsByGroupId(Integer gid);

    /**
     * 根据用户的id和群组的id修改信息，只更新非空字段
     * @param membership 至少需要包含用户id和群组id
     * @return 受影响的行数
     */
    int updateMembershipByUGSelectively(Membership membership);

    /**
     * 根据id修改信息，只更新非空字段
     * @param membership 至少需要包含id
     * @return 受影响的行数
     */
    int updateMembershipByIdSelectively(Membership membership);

    /**
     * 根据id查找记录
     * @param id 记录的id
     * @return 查询结果
     */
    Membership selectMembershipById(Integer id);

    /**
     * 根据id查找记录，只查询公开信息
     * @param id 记录的id
     * @return 查询结果
     */
    Membership selectMembershipPubInfoById(Integer id);

    /**
     * 根据用户的id和群组的id查询信息
     * @param uid 用户的id
     * @param gid 群组的id
     * @return 查询结果
     */
    Membership selectMembershipByUG(@Param("uid") Integer uid, @Param("gid") Integer gid);

    /**
     * 根据用户的id和群组的id查询信息，只查询公开信息
     * @param uid 用户的id
     * @param gid 群组的id
     * @return 查询结果
     */
    Membership selectMembershipPubInfoByUG(@Param("uid") Integer uid, @Param("gid") Integer gid);

    /**
     * 根据用户id查询他加入的群
     * @param uid 用户的id
     * @return 查询结果
     */
    List<Membership> selectMembershipsByUserId(Integer uid);

    /**
     * 根据用户id查询他加入的群，只查询公开信息
     * @param uid 用户的id
     * @return 查询结果
     */
    List<Membership> selectMembershipsPubInfoByUserId(Integer uid);

    /**
     * 查询某个群中的所有成员
     * @param gid 群组的id
     * @return 查询结果
     */
    List<Membership> selectMembershipsByGroupId(Integer gid);

    /**
     * 查询某个群中的所有成员，只查询公开信息
     * @param gid 群组的id
     * @return 查询结果
     */
    List<Membership> selectMembershipsPubInfoByGroupId(Integer gid);
}