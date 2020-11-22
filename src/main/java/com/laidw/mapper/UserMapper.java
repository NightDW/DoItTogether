package com.laidw.mapper;

import com.laidw.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 针对User的增删改查操作
 */
@Mapper
public interface UserMapper {

    /**
     * 插入数据
     * @param user 插入的数据
     * @return 受影响的行数
     */
    int insertUser(User user);

    /**
     * 根据id删除数据
     * @param id 用户的id
     * @return 受影响的行数
     */
    int deleteUserById(Integer id);

    /**
     * 根据id更新数据
     * @param user 至少包含用户的id
     * @return 受影响的行数
     */
    int updateUserById(User user);

    /**
     * 根据id更新数据，只更新非空字段
     * @param user 至少包含用户的id
     * @return 受影响的行数
     */
    int updateUserByIdSelectively(User user);

    /**
     * 根据id查询用户
     * @param id 用户的id
     * @return 查询结果
     */
    User selectUserById(Integer id);

    /**
     * 根据id查询用户的公开信息
     * @param id 用户的id
     * @return 查询结果
     */
    User selectUserPubInfoById(Integer id);

    /**
     * 根据根据特定条件查询用户，优先级为id > username > email > phone
     * 也就是如果id不为空，则根据id进行查询；否则再考虑用username和email查询
     * @param user 用户的信息
     * @return 查询结果
     */
    User selectUserByCondition(User user);

    /**
     * 根据根据特定条件查询用户的公开信息，优先级为id > username > email > phone
     * 也就是如果id不为空，则根据id进行查询；否则再考虑用username和email查询
     * @param user 用户的信息
     * @return 查询结果
     */
    User selectUserPubInfoByCondition(User user);

    /**
     * 查询所有用户
     * @return 查询结果
     */
    List<User> selectAllUsers();

    /**
     * 模糊查询，查询用户名/个性签名含有指定关键字的用户，关键字需要自己拼接百分号
     * @param key 关键词，需要有百分号
     * @return 查询结果
     */
    List<User> searchUsersLike(String key);

    /**
     * 精准查询，查找范围包括用户名，个性签名，电话，邮箱
     * @param key 关键词
     * @return 查询结果
     */
    List<User> searchUserByKey(String key);
}