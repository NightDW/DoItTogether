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

@Service
@Transactional
public class MessageServiceImpl implements MessageService {

    @Autowired
    private MessageMapper messageMapper;

    @Autowired
    private User systemUser;

    public int sendSystemMsgTo(Integer uid, String content) {
        return sendMsg(true, systemUser.getId(), uid, content);
    }

    public int sendMsg(Boolean isPrivate, Integer senderId, Integer acceptorId, String content) {
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

    public int deleteMessagesBetween(Integer id1, Integer id2) {
        return messageMapper.deleteMessagesBetween(id1, id2);
    }

    public int deleteMessagesByGroupId(Integer gid) {
        return messageMapper.deleteMessagesByGroupId(gid);
    }

    public int updateGroupMessageSenderId(Integer oldId, Integer newId, Integer groupId) {
        return messageMapper.updateGroupMessageSenderId(oldId, newId, groupId);
    }

    public List<Message> selectMessagesByGroupId(Integer gid) {
        return messageMapper.selectMessagesByGroupId(gid);
    }

    public List<Message> selectMessagesBetween(Integer id1, Integer id2) {
        return messageMapper.selectMessagesBetween(id1, id2);
    }
}
