package com.laidw.service;

import com.laidw.entity.User;

import java.util.List;

/**
 * 主要负责User相关的业务，部分方法的作用可以参考UserMapper
 */
public interface UserService {

    void insertUser(User user);

    int deleteUserById(Integer id);

    int updateUserById(User user, Boolean isSelectively);

    User selectUserByCondition(User user, Boolean onlyPubInfo);

    List<User> selectAllUsers();

    List<User> searchUsers(String key, Boolean rough);
}
