package com.laidw.service;

import com.laidw.entity.Group;

import java.util.List;

public interface GroupService {

    void foundGroup(Integer founderId, Group group);

    void deleteGroupById(Integer id);

    int updateGroupByIdSelectively(Group group);

    Group selectGroupById(Integer id, Boolean onlyPubInfo);

    List<Group> searchGroups(String key, Boolean rough);
}
