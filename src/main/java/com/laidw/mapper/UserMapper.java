package com.laidw.mapper;

import com.laidw.entity.User;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 针对User的增删改查操作
 */
@Mapper
public interface UserMapper {

    //插入一条数据，id自动生成
    int insertUser(User user);

    //根据id删除数据
    int deleteUserById(Integer id);

    //根据user.id找到相应的记录并更新该记录的全部字段
    int updateUserById(User user);

    //根据user.id找到相应的记录，只更新非空字段
    int updateUserByIdSelectively(User user);

    User selectUserById(Integer id);

    //根据特定条件查询用户的公开信息
    User selectUserPubInfoById(Integer id);

    //根据特定条件查询用户
    //优先级为id > username > email > phone
    //也就是如果id不为空，则根据id进行查询；否则再考虑用username和email查询
    User selectUserByCondition(User user);

    //根据特定条件查询用户的公开信息
    User selectUserPubInfoByCondition(User user);

    //查询所有用户
    List<User> selectAllUsers();

    //模糊查询，查询用户名/个性签名含有指定关键字的用户，关键字需要自己拼接百分号
    List<User> searchUsersLike(String key);

    //精准查询，查找范围包括用户名，个性签名，电话，邮箱
    List<User> searchUserByKey(String key);
}
