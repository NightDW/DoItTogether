package com.laidw.entity;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 用于保存主用户与客用户的好友关系：客用户是主用户的好友，但反过来不一定
 * 为了保护其他人的隐私，用户只能查询主用户为自己的好友关系
 * 注意，本类实现了Comparable接口，用于进行排序
 */
@Getter@Setter
public class Friendship implements Comparable<Friendship> {

    /**
     * 该记录的id
     */
    private Integer id;

    /**
     * 主用户id，由于主用户就是当前登录的用户本身，因此保存其id以方便查找数据库即可
     */
    private Integer hostId;

    /**
     * 客用户，注意这里的user对象只有一些基本信息，客用户的隐私信息不会被查询出来
     * 隐私信息包括该用户的好友、加入的群组、接收的任务等
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

    /**
     * 实现比较方法，value值更大的Friendship记录将在好友列表更靠前的地方显示
     * @param o 与当前对象进行比较的对象
     * @return 返回o.getValue() - getValue()表示将根据value值进行从大到小的排序
     */
    public int compareTo(Friendship o) {
        return o.getValue() - getValue();
    }

    /**
     * 获取Friendship对象的value值，value值越大，显示越靠前
     * 对象默认按id值从小到大进行显示，因此id越小，对象的value值越大
     * 如果置顶，则value值加上100000；如果隐藏，则value值减去1000000
     * @return Friendship的value值
     */
    public int getValue(){
        return -id + (isTop ? 100000 : 0) - (isShow ? 0 : 1000000);
    }
}
