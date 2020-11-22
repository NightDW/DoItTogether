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

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User condition = new User();
        condition.setUsername(username);
        User user = userService.selectUserByCondition(condition, true);
        if(user == null)
            throw new UsernameNotFoundException("Username no exist!");
        return user;
    }
}