package com.laidw.service.impl;

import com.laidw.entity.Friendship;
import com.laidw.entity.Message;
import com.laidw.entity.User;
import com.laidw.mapper.FriendshipMapper;
import com.laidw.service.FriendshipService;
import com.laidw.service.MessageService;
import com.laidw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
@Transactional
public class FriendshipServiceImpl implements FriendshipService {

    @Autowired
    private FriendshipMapper friendshipMapper;

    @Autowired
    private UserService userService;

    @Autowired
    private MessageService messageService;

    @Value("${dit.system-user.become-friend-msg}")
    private String becomeFriendMsg;

    public int insertFriendship(Integer hid, Integer gid, String remarks) {
        Friendship friendship = new Friendship();
        friendship.setIsShow(true);
        friendship.setIsTop(false);
        friendship.setHostId(hid);
        friendship.setGuestPubInfo(new User(gid));
        friendship.setRemarks(remarks);
        return friendshipMapper.insertFriendship(friendship);
    }

    public void requestToBecomeFriends(Integer hid, Integer gid, String msg) {
        User host = userService.selectUserByCondition(new User(hid), true);
        messageService.sendSystemMsgTo(gid, msg);
    }

    public void agreeToBecomeFriends(Integer hid, Integer gid) {
        insertFriendship(hid, gid, null);
        insertFriendship(gid, hid, null);
        messageService.sendMsg(true, hid, gid, becomeFriendMsg);
    }

    public void deleteFriend(Integer hid, Integer gid) {
        messageService.deleteMessagesBetween(hid, gid);
        friendshipMapper.deleteFriendship(hid, gid);
        friendshipMapper.deleteFriendship(gid, hid);
    }

    public int updateFriendshipSelectively(Friendship friendship) {
        return friendshipMapper.updateFriendshipSelectively(friendship);
    }

    public int updateFriendshipByIdSelectively(Friendship friendship) {
        return friendshipMapper.updateFriendshipByIdSelectively(friendship);
    }

    public Friendship selectFriendshipById(Integer id) {
        return friendshipMapper.selectFriendshipById(id);
    }

    public Friendship selectFriendship(Integer hid, Integer gid) {
        return friendshipMapper.selectFriendship(hid, gid);
    }

    public List<Friendship> selectFriendshipsByHostId(Integer hid) {
        return friendshipMapper.selectFriendshipsByHostId(hid);
    }
}
