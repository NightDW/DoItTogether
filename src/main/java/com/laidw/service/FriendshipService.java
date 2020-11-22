package com.laidw.service;

import com.laidw.entity.Friendship;

import java.util.List;

/**
 * 主要负责Friendship相关的业务，部分方法的作用可以参考FriendshipMapper
 */
public interface FriendshipService {

    int insertFriendship(Integer hid, Integer gid, String remarks);

    /**
     * 同时插入两条Friendship数据
     * @param uid1 用户1的id
     * @param uid2 用户2的id
     */
    void insertFriendshipPair(Integer uid1, Integer uid2);

    void deleteFriendshipPairByHG(Integer hid, Integer gid);

    int updateFriendshipByHGSelectively(Friendship friendship);

    int updateFriendshipByIdSelectively(Friendship friendship);

    Friendship selectFriendshipById(Integer id);

    Friendship selectFriendshipByHG(Integer hid, Integer gid);

    List<Friendship> selectFriendshipsByHostId(Integer hid);
}
