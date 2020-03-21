package com.laidw.service;

import com.laidw.entity.User;

import java.util.List;

public interface UserService {

    void insertUser(User user);

    int deleteUserById(Integer id);

    int updateUserById(User user, Boolean isSelectively);

    User selectUserByCondition(User user, Boolean onlyPubInfo);

    List<User> selectAllUsers();

    List<User> searchUsers(String key, Boolean rough);
}
