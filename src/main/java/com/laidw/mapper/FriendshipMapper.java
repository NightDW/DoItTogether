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

    //host将guest添加为好友时调用该方法
    int insertFriendship(Friendship friendship);

    //删除方法
    int deleteFriendship(@Param("hid") Integer hid, @Param("gid") Integer gid);

    //根据hostId和guestId有选择性地修改信息
    int updateFriendshipSelectively(Friendship friendship);

    int updateFriendshipByIdSelectively(Friendship friendship);

    Friendship selectFriendshipById(Integer id);

    //根据hostId和guestId查询记录
    Friendship selectFriendship(@Param("hid") Integer hid, @Param("gid") Integer gid);

    //查找某个人的所有好友
    List<Friendship> selectFriendshipsByHostId(Integer hid);
}
