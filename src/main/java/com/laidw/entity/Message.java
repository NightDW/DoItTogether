package com.laidw.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * 用于保存消息的信息，既可以表示用户与用户之间的私聊消息，也可以表示用户在群组中的发言
 *
 * 每个消息都有发送者和接收者，其中：
 *   1. 接收者属性主要是用来查询的（比如查询某人接收到的所有消息），因此只需要用相应的id来表示即可
 *   2. 发送者属性则可以让接收者查看该消息是什么人发送的，因此需要有较为详细的信息
 *
 * 如果是群内发言，则需要保存：
 *   1. 接收者（即所在群）的GroupId
 *   2. 发送者在该群中的Membership信息（而不是简单的User信息）
 *
 * 如果是私聊信息，则需要保存：
 *   1. 接收者（本人或私聊的另一个用户）的UserId
 *   2. 发送者的User信息
 */
@Getter@Setter
public class Message {

    /**
     * 消息的id
     */
    private Integer id;

    /**
     * 消息的性质，true表示是与另一个用户的私聊，false表示群内留言
     */
    private Boolean isPrivate;

    /**
     * 消息接收者的id
     * isPrivate == false 时代表所在群的groupId
     * isPrivate == true 时代表被私聊用户的userId
     */
    private Integer accId;

    /**
     * 消息的发送者的公开信息
     * isPrivate == false 时可强转为Membership类型
     * isPrivate == true 时可强转为User类型
     */
    private Object senderPubInfo;

    /**
     * 留言的发送时间
     */
    private Date sendTime;

    /**
     * 留言的内容
     */
    private String content;
}
