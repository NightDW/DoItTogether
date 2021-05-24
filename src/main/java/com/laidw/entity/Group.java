package com.laidw.entity;

import lombok.*;

import java.util.List;

/**
 * 用于保存任务群组的信息
 */
@Getter@Setter
@NoArgsConstructor
public class Group {

    /**
     * 群组的id
     */
    private Integer id;

    /**
     * 群组的名称
     */
    private String name;

    /**
     * 群公告
     */
    private String slogan;

    /**
     * 群组的头像地址
     */
    private String iconUrl;

    /**
     * 群组的成员
     */
    private List<Membership> memberships;

    /**
     * 群组内的留言
     */
    private List<Message> messages;

    /**
     * 群组内的任务
     */
    private List<Task> tasks;

    public Group(Integer id) {
        this.id = id;
    }
}