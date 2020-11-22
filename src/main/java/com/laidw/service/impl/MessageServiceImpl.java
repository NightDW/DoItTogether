package com.laidw.service.impl;

import com.laidw.entity.Membership;
import com.laidw.entity.Message;
import com.laidw.entity.User;
import com.laidw.mapper.MessageMapper;
import com.laidw.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * MessageService的实现类
 */
@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private User systemUser;


    public int sendSystemMsgTo(Integer uid, String content) {
        return sendMsgReturnId(true, systemUser.getId(), uid, content);
    }

    public int sendMsgReturnId(Boolean isPrivate, Integer senderId, Integer acceptorId, String content) {
        Message msg = new Message();
        msg.setIsPrivate(isPrivate);
        msg.setAccId(acceptorId);
        msg.setSenderPubInfo(isPrivate ? new User(senderId) : new Membership(senderId));
        msg.setSendTime(new Date());
        msg.setContent(content);
        messageMapper.insertMessage(msg);
        return msg.getId();
    }

    public int deleteMessageById(Integer id) {
        return messageMapper.deleteMessageById(id);
    }

    public int deletePrivateMessagesBetween(Integer id1, Integer id2) {
        return messageMapper.deletePrivateMessagesBetween(id1, id2);
    }

    public int deleteGroupMessagesByGroupId(Integer gid) {
        return messageMapper.deleteGroupMessagesByGroupId(gid);
    }

    public int updateGroupMessageSenderId(Integer oldId, Integer newId, Integer groupId) {
        return messageMapper.updateGroupMessageSenderId(oldId, newId, groupId);
    }

    public Message selectMessageById(Integer id) {
        return messageMapper.selectMessageById(id);
    }

    public List<Message> selectGroupMessagesByGroupId(Integer gid) {
        return messageMapper.selectGroupMessagesByGroupId(gid);
    }

    public List<Message> selectPrivateMessagesBetween(Integer id1, Integer id2) {
        return messageMapper.selectPrivateMessagesBetween(id1, id2);
    }
}
