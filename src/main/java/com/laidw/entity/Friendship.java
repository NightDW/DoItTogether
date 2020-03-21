package com.laidw.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * 用于保存主用户与客用户之间的好友关系
 * 为了保护其他人的隐私，用户只能查询主用户为自己的好友关系
 * 也就是用户可以查询他有哪些好友，但不能查询其它用户有哪些好友
 */
@Getter@Setter
public class Friendship implements Comparable<Friendship> {

    /**
     * 该记录的id
     */
    private Integer id;

    /**
     * 主用户id，由于主用户就是当前登录的用户本身，因此保存其id即可
     */
    private Integer hostId;

    /**
     * 客用户，注意这里的user对象只有一些基本信息，客用户的隐私信息不会被查询出来
     */
    private User guestPubInfo;

    /**
     * 主用户给客用户取的备注（类似于微信的备注），方便主用户识别客用户
     */
    private String remarks;

    /**
     * 两个用户之间的私聊是否在主用户的聊天界面中显示
     */
    private Boolean isShow;

    /**
     * 两个用户之间的私聊是否在主用户的聊天界面中置顶
     */
    private Boolean isTop;

    /**
     * 主客用户之间的私聊信息
     */
    private List<Message> messages;

    public int getValue(){
        return - id + (isTop ? 1000 : 0) - (isShow ? 0 : 10000);
    }

    public int compareTo(Friendship o) {
        return o.getValue() - getValue();
    }
}
