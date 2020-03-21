package com.laidw.mapper;

import com.laidw.entity.Friendship;
import com.laidw.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class FriendshipMapperTest {

    @Autowired
    private FriendshipMapper friendshipMapper;

    @Test
    public void insertFriendshipTest(){
        Friendship friendship = new Friendship();
        User user = new User();
        friendship.setGuestPubInfo(user);
        friendship.setIsShow(true);
        friendship.setIsTop(false);
        for(int i = 1; i < 7; i++){
            friendship.setHostId(i);
            user.setId(i + 1);
            friendship.setRemarks(i + 1 + " is " + i + "'s friend");
            friendshipMapper.insertFriendship(friendship);
        }
    }

    @Test
    public void updateFriendshipSelectivelyTest(){
        Friendship friendship = new Friendship();
        friendship.setHostId(6);
        friendship.setGuestPubInfo(new User(7));
        friendship.setIsTop(true);
        friendship.setRemarks("n_remarks");
        friendshipMapper.updateFriendshipSelectively(friendship);
    }

    @Test
    public void deleteFriendshipTest(){
        friendshipMapper.deleteFriendship(6, 7);
    }

    @Test
    public void selectFriendshipTest() {
        Friendship friendship = friendshipMapper.selectFriendship(1, 2);
        List<Friendship> list = friendshipMapper.selectFriendshipsByHostId(4);
        list = null;
    }
}
