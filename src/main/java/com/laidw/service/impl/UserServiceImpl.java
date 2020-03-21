package com.laidw.service.impl;

import com.laidw.entity.User;
import com.laidw.mapper.UserMapper;
import com.laidw.service.FriendshipService;
import com.laidw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private User systemUser;

    @Value("${dit.default-user-img}")
    private String userImg;

    public void insertUser(User user) {
        if(user.getIconUrl() == null)
            user.setIconUrl(userImg);
        userMapper.insertUser(user);
        friendshipService.insertFriendship(user.getId(), systemUser.getId(), null);
    }

    public int deleteUserById(Integer id) {
        return userMapper.deleteUserById(id);
    }

    public int updateUserById(User user, Boolean isSelectively) {
        if(isSelectively)
            return userMapper.updateUserByIdSelectively(user);
        return userMapper.updateUserById(user);
    }

    public User selectUserByCondition(User user, Boolean onlyPubInfo) {
        if(onlyPubInfo)
            return userMapper.selectUserPubInfoByCondition(user);
        return userMapper.selectUserByCondition(user);
    }

    public List<User> selectAllUsers() {
        return userMapper.selectAllUsers();
    }

    public List<User> searchUsers(String key, Boolean rough) {
        if(rough)
            return userMapper.searchUsersLike("%" + key + "%");
        return userMapper.searchUserByKey(key);
    }
}
