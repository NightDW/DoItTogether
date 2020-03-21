package com.laidw.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Date;

/**
 * 用于保存留言信息
 */
@Getter@Setter
public class Message {
    /**
     * 留言的id
     */
    private Integer id;

    /**
     * 留言的性质，true表示是与另一个用户的私聊，false表示群内留言
     */
    private Boolean isPrivate;

    /**
     * 留言接收者的id
     * isPrivate == true 时代表被私聊对象的userId
     * isPrivate == false 时代表所在群的groupId
     */
    private Integer accId;

    /**
     * 留言的发送者的公开信息
     * isPrivate == true 时可强转为User类型
     * isPrivate == false 时可强转为Membership类型
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
