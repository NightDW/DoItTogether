package com.laidw.service;

import com.laidw.entity.Message;

import java.util.List;

/**
 * 主要负责Message相关的业务，部分方法的作用可以参考MessageMapper
 */
public interface MessageService {

    int sendSystemMsgTo(Integer uid, String content);

    int sendMsgReturnId(Boolean isPrivate, Integer senderId, Integer acceptorId, String content);

    int deleteMessageById(Integer id);

    int deletePrivateMessagesBetween(Integer id1, Integer id2);

    int deleteGroupMessagesByGroupId(Integer gid);

    int updateGroupMessageSenderId(Integer oldId, Integer newId, Integer groupId);

    Message selectMessageById(Integer id);

    List<Message> selectGroupMessagesByGroupId(Integer gid);

    List<Message> selectPrivateMessagesBetween(Integer id1, Integer id2);
}
