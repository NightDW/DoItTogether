package com.laidw.entity;

import com.laidw.entity.common.Role;
import lombok.*;

/**
 * 用于保存用户在某个群组内的信息
 * 该类同样实现了Comparable接口，用于排序
 */
@Getter@Setter
@NoArgsConstructor
public class Membership implements Comparable<Membership>{

    /**
     * 该记录的id
     */
    private Integer id;

    /**
     * 为了防止嵌套查询，同时保护隐私，这里只保存用户的公开信息
     */
    private User userPubInfo;

    /**
     * 为了防止嵌套查询，同时保护隐私，这里只保存用户所在的群组的公开信息
     */
    private Group groupPubInfo;

    /**
     * 用户在本群中的角色，注意和User类中的角色区分开
     */
    private Role role;

    /**
     * 用户在本群中的昵称
     */
    private String nickname;

    /**
     * 用户在本群中是否被禁言
     */
    private Boolean isMute;

    /**
     * 该群在该用户的聊天界面中是否显示
     */
    private Boolean isShow;

    /**
     * 该群在该用户的聊天界面中是否置顶
     */
    private Boolean isTop;

    /**
     * 实现比较方法
     * @param o 被比较的对象
     * @return 返回值表示要根据value值从大到小进行排序
     */
    public int compareTo(Membership o) {
        return o.getValue() - getValue();
    }

    /**
     * 根据id isTop isShow来计算该Membership的评价值，评价值用于排序
     * @return 该对象的评价值，评价值越高排序应该越靠前
     */
    public int getValue(){
        return - id + (isTop ? 100000 : 0) - (isShow ? 0 : 1000000);
    }

    public Membership(Integer id) {
        this.id = id;
    }
}
