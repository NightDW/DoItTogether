package com.laidw.config;

import com.laidw.entity.User;
import com.laidw.entity.common.Role;
import com.laidw.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 用于将系统通知用户（DIT小助手）从数据库中查询出来并放入IOC容器
 * 系统通知用户主要用于给其他用户发送系统消息
 * 另外，当用户退出某个群组时，系统通知用户将会替代该用户，并继承该用户在该群中的所有数据
 */
@Configuration
public class SystemUserConfig {

    @Autowired
    private UserMapper userMapper;

    @Value("${dit.system-user.username}")
    private String username;

    @Value("${dit.system-user.password}")
    private String password;

    @Value("${dit.system-user.introduction}")
    private String introduction;

    @Value("${dit.system-user.icon-url}")
    private String iconUrl;

    @Value("${dit.system-user.email}")
    private String email;

    @Value("${dit.system-user.phone}")
    private String phone;

    @Value("${dit.system-user.gender}")
    private Boolean gender;

    @Value("${dit.system-user.role}")
    private String roleName;

    @Value("${dit.system-user.is-valid}")
    private Boolean isValid;

    @Value("${dit.system-user.verify-code}")
    private String verifyCode;

    @Bean
    public User getSystemUser(){

        //查询数据库中是否存在系统用户
        User condition = new User();
        condition.setRole(Role.SYSTEM);
        User user = userMapper.selectUserPubInfoByCondition(condition);

        //如果存在，则直接将查询结果放到IOC容器中并返回
        if(user != null)
            return user;

        //将配置文件中关于系统用户的配置封装成一个User对象
        User systemUserTemplate = new User();
        systemUserTemplate.setUsername(username);
        systemUserTemplate.setPassword(password);
        systemUserTemplate.setIntroduction(introduction);
        systemUserTemplate.setIconUrl(iconUrl);
        systemUserTemplate.setEmail(email);
        systemUserTemplate.setPhone(phone);
        systemUserTemplate.setGender(gender);
        systemUserTemplate.setRole(Role.valueOf(roleName));
        systemUserTemplate.setIsValid(isValid);
        systemUserTemplate.setVerifyCode(verifyCode);

        //将系统用户数据插入数据库中，然后将该对象放到IOC容器中
        userMapper.insertUser(systemUserTemplate);
        return systemUserTemplate;
    }
}
