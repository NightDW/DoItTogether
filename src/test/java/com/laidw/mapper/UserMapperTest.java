package com.laidw.mapper;

import com.laidw.entity.User;
import com.laidw.entity.common.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void insertUserTest(){
        User user = new User();
        String username = "laidw";
        user.setPassword("password");
        user.setIconUrl("icon_url");
        user.setGender(true);
        user.setRole(Role.ADMIN);
        user.setIsValid(true);
        user.setVerifyCode("verify_code");
        for(int i = 0; i < 10; i++){
            user.setUsername(username + i);
            user.setEmail(username + i + "@qq.com");
            user.setPhone("" + i + i + i + i + i);
            userMapper.insertUser(user);
        }
    }

    @Test
    public void updateUserByIdTest(){
        User user = new User();
        user.setId(10);
        user.setUsername("n_laidw");
        user.setPassword("n_password");
        user.setEmail("n_email");
        user.setPhone("n_phone");
        user.setGender(false);
        user.setRole(Role.FOUNDER);
        user.setIsValid(false);
        user.setVerifyCode("n_verify_code");
        userMapper.updateUserById(user);
    }

    @Test
    public void updateUserByIdSelectivelyTest(){
        User user = new User();
        user.setId(9);
        user.setRole(Role.NORMAL);
        userMapper.updateUserByIdSelectively(user);
    }

    @Test
    public void deleteUserByIdTest(){
        userMapper.deleteUserById(9);
        userMapper.deleteUserById(10);
    }

    @Test
    public void selectUserTest(){
        User demo = new User();

        demo.setEmail("laidw0@qq.com");
        User user1 = userMapper.selectUserByCondition(demo);

        demo.setUsername("laidw1");
        User user2 = userMapper.selectUserPubInfoByCondition(demo);

        demo.setId(4);
        User user4 = userMapper.selectUserByCondition(demo);

        userMapper.selectAllUsers();
    }
}
