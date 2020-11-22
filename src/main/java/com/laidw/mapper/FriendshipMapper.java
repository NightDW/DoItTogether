package com.laidw.mapper;

import com.laidw.entity.Friendship;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 针对Friendship的增删改查操作
 */
@Mapper
public interface FriendshipMapper {

    /**
     * 插入方法
     * @param friendship 插入的信息，至少需要提供hostId和guestId
     * @return 受影响的行数
     */
    int insertFriendship(Friendship friendship);

    /**
     * 根据hostId和guestId进行删除
     * @param hid hostId
     * @param gid guestId
     * @return 受影响的行数
     */
    int deleteFriendshipByHG(@Param("hid") Integer hid, @Param("gid") Integer gid);

    /**
     * 根据hostId和guestId有选择性地修改信息
     * @param friendship 至少需要提供hostId和guestId
     * @return 受影响的行数
     */
    int updateFriendshipByHGSelectively(Friendship friendship);

    /**
     * 根据id有选择性地修改信息
     * @param friendship 至少需要提供id
     * @return 受影响的行数
     */
    int updateFriendshipByIdSelectively(Friendship friendship);

    /**
     * 根据id查找Friendship
     * @param id Friendship的id
     * @return 查找结果
     */
    Friendship selectFriendshipById(Integer id);

    /**
     * 根据hostId和guestId查询Friendship
     * @param hid hostId
     * @param gid guestId
     * @return 查找结果
     */
    Friendship selectFriendshipByHG(@Param("hid") Integer hid, @Param("gid") Integer gid);

    /**
     * 查找某个人的所有好友
     * @param hid hostId
     * @return 查找结果
     */
    List<Friendship> selectFriendshipsByHostId(Integer hid);
}
