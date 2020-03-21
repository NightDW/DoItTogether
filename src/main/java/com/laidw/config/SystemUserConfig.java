package com.laidw.config;

import com.laidw.entity.User;
import com.laidw.entity.common.Role;
import com.laidw.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * 用于配置系统通知用户，由于涉及的代码较多，因此单独抽取到一个配置类中
 */
@Configuration
public class SystemUserConfig {

    @Autowired
    private UserMapper userMapper;

    @Value("${dit.system-user.username}")
    private String username;

    @Value("${dit.system-user.password}")
    private String password;

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

        //查询数据库中是否存在名称为指定用户名的用户
        User condition = new User();
        condition.setUsername(username);
        User user = userMapper.selectUserPubInfoByCondition(condition);

        //如果存在，则将查询结果返回
        if(user != null){
            //System.out.println("System User Id: " + user.getId());
            return user;
        }

        //如果不存在，则插入数据后把结果返回
        User systemUserTemplate = new User();
        systemUserTemplate.setUsername(username);
        systemUserTemplate.setPassword(password);
        systemUserTemplate.setIconUrl(iconUrl);
        systemUserTemplate.setEmail(email);
        systemUserTemplate.setPhone(phone);
        systemUserTemplate.setGender(gender);
        systemUserTemplate.setRole(Role.valueOf(roleName));
        systemUserTemplate.setIsValid(isValid);
        systemUserTemplate.setVerifyCode(verifyCode);
        userMapper.insertUser(systemUserTemplate);
        //System.out.println("System User Id: " + systemUserTemplate.getId());
        return systemUserTemplate;
    }
}
