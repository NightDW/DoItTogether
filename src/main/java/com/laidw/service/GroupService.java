package com.laidw.service;

import com.laidw.entity.Group;

import java.util.List;

/**
 * 主要负责Group相关的业务，部分方法的作用可以参考GroupMapper
 */
public interface GroupService {

    /**
     * 创建群组时调用该方法
     * @param founderId 创建者的id
     * @param group 群组的信息
     */
    void foundGroup(Integer founderId, Group group);

    void deleteGroupById(Integer id);

    int updateGroupByIdSelectively(Group group);

    Group selectGroupById(Integer id, Boolean onlyPubInfo);

    List<Group> searchGroups(String key, Boolean rough);
}
