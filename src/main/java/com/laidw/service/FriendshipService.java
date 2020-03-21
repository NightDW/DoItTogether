package com.laidw.service;

import com.laidw.entity.Friendship;

import java.util.List;

public interface FriendshipService {

    int insertFriendship(Integer hid, Integer gid, String remarks);

    void requestToBecomeFriends(Integer hid, Integer gid, String msg);

    void agreeToBecomeFriends(Integer hid, Integer gid);

    void deleteFriend(Integer hid, Integer gid);

    int updateFriendshipSelectively(Friendship friendship);

    int updateFriendshipByIdSelectively(Friendship friendship);

    Friendship selectFriendshipById(Integer id);

    Friendship selectFriendship(Integer hid, Integer gid);

    List<Friendship> selectFriendshipsByHostId(Integer hid);
}
