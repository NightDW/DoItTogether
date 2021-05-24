package com.laidw.service.impl;

import com.laidw.entity.User;
import com.laidw.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * UserDetailsService的实现类，SpringSecurity通过该类来验证是否登录成功
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserService userService;

    /**
     * SpringSecurity通过该方法来获取某个用户名对应的用户信息
     * @param username 用户名
     * @return 用户信息
     * @throws UsernameNotFoundException 查找不到指定的用户名时抛出该异常
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User condition = new User();
        condition.setUsername(username);
        User user = userService.selectUserByCondition(condition, true);
        if(user == null)
            throw new UsernameNotFoundException("Username no exist!");
        return user;
    }
}