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

/**
 * 测试MessageMapper
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class MessageMapperTest {

    @Autowired
    private MessageMapper messageMapper;

    @Test
    public void insertMessageTest(){
        User senderUser = new User();
        Membership senderMem = new Membership();
        Message message = new Message();
        message.setContent("Hello");
        message.setSendTime(new Date());

        message.setIsPrivate(false);
        message.setSenderPubInfo(senderMem);
        for(int i = 1; i < 4; i++){
            senderMem.setId(i);
            message.setAccId(i);
            messageMapper.insertMessage(message);
        }

        message.setIsPrivate(true);
        message.setSenderPubInfo(senderUser);
        for(int i = 1; i < 6; i++){
            senderUser.setId(i);
            message.setAccId(i + 1);
            messageMapper.insertMessage(message);
        }
    }

    @Test
    public void updateMessageTest(){
        messageMapper.updateGroupMessageSenderId(1, 2, 1);
    }

    @Test
    public void deleteMessageTest(){
        messageMapper.deleteMessageById(1);
        messageMapper.deleteGroupMessagesByGroupId(2);
        messageMapper.deletePrivateMessagesBetween(5, 6);
    }

    /*
     * 查询方法测试比较复杂，因此暂时忽略
     */
}
