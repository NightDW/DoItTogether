package com.laidw.mapper;

import com.laidw.entity.Membership;
import com.laidw.entity.Message;
import com.laidw.entity.User;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.Date;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
public class MessageMapperTest {

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void insertMessageTest(){
        Message message = new Message();
        User user = new User();
        Membership membership = new Membership();
        membership.setUserPubInfo(user);
        message.setContent("Hello");
        message.setSendTime(new Date());
        for(int i = 1; i < 4; i++){
            message.setIsPrivate(false);
            message.setSenderPubInfo(membership);
            user.setId(i);
            message.setAccId(i);
            messageMapper.insertMessage(message);
        }
        for(int i = 4; i < 8; i++){
            message.setIsPrivate(true);
            message.setSenderPubInfo(user);
            user.setId(i);
            message.setAccId(i + 1);
            messageMapper.insertMessage(message);
        }
    }

    @Test
    public void deleteMessageByIdTest(){
        messageMapper.deleteMessageById(7);
    }

    @Test
    public void selectMessageTest(){
        List<Message> list1 = messageMapper.selectMessagesByGroupId(3);
        List<Message> list2 = messageMapper.selectMessagesBetween(5, 4);
        list1 = list2 = null;
    }
}
