package com.laidw.mapper;

import com.laidw.entity.Group;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 针对Group的增删改查操作
 */
@Mapper
public interface GroupMapper {

    /**
     * 插入方法
     * @param group 插入的数据
     * @return 受影响的行数
     */
    int insertGroup(Group group);

    /**
     * 根据id删除记录
     * @param id 被删除的记录的id
     * @return 受影响的行数
     */
    int deleteGroupById(Integer id);

    /**
     * 根据id有选择性地更新数据
     * @param group 至少需要有id
     * @return 受影响的行数
     */
    int updateGroupByIdSelectively(Group group);

    /**
     * 根据id查询记录
     * @param id 记录的id
     * @return 查询结果
     */
    Group selectGroupById(Integer id);

    /**
     * 根据id查询数据，只查询公开数据
     * @param id 记录的id
     * @return 查询结果
     */
    Group selectGroupPubInfoById(Integer id);

    /**
     * 模糊查询，查询群名称或群公告含有指定关键字的群组，关键字需要自己拼接百分号
     * @param key 查询的关键字，需要带有百分号
     * @return 查询结果
     */
    List<Group> searchGroupsLike(String key);

    /**
     * 精准查询
     * @param key 查询的关键字
     * @return 查询结果
     */
    List<Group> searchGroupsByKey(String key);
}