package com.laidw.entity.common;

/**
 * 枚举一些常用的角色
 * 既可以表示群成员在群组内部的角色，也可以表示用户在本平台上的角色
 */
public enum Role {

    /*
     * SYSTEM表示系统用户（专门给其他用户发送系统消息的），不能用户群组内部
     * FOUNDER表示群组的建立者，当然，也可以用于表示本平台的创建者（但很少用）
     * ADMIN表示群组的管理员，也可以用户表示本平台的管理员（可对其它账户实施冻结等操作，但该功能尚未实现）
     * NORMAL表示普通的群成员或普通的平台用户
     */
    SYSTEM, FOUNDER, ADMIN, NORMAL;
}
