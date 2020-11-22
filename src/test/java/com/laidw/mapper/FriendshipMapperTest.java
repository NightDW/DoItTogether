package com.laidw.mapper;

import com.laidw.entity.Friendship;
import com.laidw.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试FriendshipMapper
 */
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
        friendship.setRemarks("Guest is host's friend");
        for(int i = 1; i < 7; i++){
            friendship.setHostId(i);
            user.setId(i + 1);
            friendshipMapper.insertFriendship(friendship);
        }
    }

    @Test
    public void updateFriendshipTest(){
        Friendship friendship = new Friendship();
        friendship.setHostId(6);
        friendship.setGuestPubInfo(new User(7));
        friendship.setIsTop(true);
        friendship.setRemarks("n_remarks");
        friendshipMapper.updateFriendshipByHGSelectively(friendship);

        friendship.setId(5);
        friendship.setHostId(null);
        friendship.setGuestPubInfo(null);
        friendship.setIsTop(true);
        friendship.setRemarks("n_remarks");
        friendshipMapper.updateFriendshipByIdSelectively(friendship);
    }

    @Test
    public void deleteFriendshipByHGTest(){
        friendshipMapper.deleteFriendshipByHG(6, 7);
    }

    /*
     * 查询方法测试比较复杂，因此暂时忽略
     */
}
