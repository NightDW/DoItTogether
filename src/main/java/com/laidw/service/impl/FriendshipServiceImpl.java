package com.laidw.service.impl;

import com.laidw.entity.Friendship;
import com.laidw.entity.User;
import com.laidw.mapper.FriendshipMapper;
import com.laidw.service.FriendshipService;
import com.laidw.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * FriendshipService的实现类
 */
@Service
@Transactional
public class FriendshipServiceImpl implements FriendshipService {

    @Autowired
    private FriendshipMapper friendshipMapper;

    @Autowired
    private MessageService messageService;

    public int insertFriendship(Integer hid, Integer gid, String remarks) {
        Friendship friendship = new Friendship();
        friendship.setIsShow(true);
        friendship.setIsTop(false);
        friendship.setHostId(hid);
        friendship.setGuestPubInfo(new User(gid));
        friendship.setRemarks(remarks);
        return friendshipMapper.insertFriendship(friendship);
    }

    public void insertFriendshipPair(Integer uid1, Integer uid2) {
        insertFriendship(uid1, uid2, null);
        insertFriendship(uid2, uid1, null);
    }

    public void deleteFriendshipPairByHG(Integer hid, Integer gid) {

        //删除好友记录前需要删除两人之间的私聊记录
        messageService.deletePrivateMessagesBetween(hid, gid);

        //注意这里需要成对删除好友记录
        friendshipMapper.deleteFriendshipByHG(hid, gid);
        friendshipMapper.deleteFriendshipByHG(gid, hid);
    }

    public int updateFriendshipByHGSelectively(Friendship friendship) {
        return friendshipMapper.updateFriendshipByHGSelectively(friendship);
    }

    public int updateFriendshipByIdSelectively(Friendship friendship) {
        return friendshipMapper.updateFriendshipByIdSelectively(friendship);
    }

    public Friendship selectFriendshipById(Integer id) {
        return friendshipMapper.selectFriendshipById(id);
    }

    public Friendship selectFriendshipByHG(Integer hid, Integer gid) {
        return friendshipMapper.selectFriendshipByHG(hid, gid);
    }

    public List<Friendship> selectFriendshipsByHostId(Integer hid) {
        return friendshipMapper.selectFriendshipsByHostId(hid);
    }
}
