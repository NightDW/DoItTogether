package com.laidw.service;

import com.laidw.entity.Message;

import java.util.List;

public interface MessageService {

    int sendSystemMsgTo(Integer uid, String content);

    //返回插入的消息的id
    int sendMsg(Boolean isPrivate, Integer senderId, Integer acceptorId, String content);

    int deleteMessageById(Integer id);

    int deleteMessagesBetween(Integer id1, Integer id2);

    int deleteMessagesByGroupId(Integer gid);

    int updateGroupMessageSenderId(Integer oldId, Integer newId, Integer groupId);

    List<Message> selectMessagesByGroupId(Integer gid);

    List<Message> selectMessagesBetween(Integer id1, Integer id2);
}
