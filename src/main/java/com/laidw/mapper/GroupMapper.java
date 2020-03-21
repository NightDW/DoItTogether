package com.laidw.mapper;

import com.laidw.entity.Group;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 针对Group的增删改查操作
 */
@Mapper
public interface GroupMapper {

    //插入一条数据
    int insertGroup(Group group);

    //根据id删除数据
    int deleteGroupById(Integer id);

    //根据id有选择性地更新数据
    int updateGroupByIdSelectively(Group group);

    //根据id查询数据
    Group selectGroupById(Integer id);

    //根据id查询数据，只查询公开数据
    Group selectGroupPubInfoById(Integer id);

    //模糊查询，查询群名称或群公告含有指定关键字的群组，关键字需要自己拼接百分号
    List<Group> searchGroupsLike(String key);

    //精准查询
    List<Group> searchGroupsByKey(String key);
}
