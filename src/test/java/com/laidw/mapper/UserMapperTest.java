package com.laidw.mapper;

import com.laidw.entity.User;
import com.laidw.entity.common.Role;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * 测试UserMapper
 */
@SpringBootTest
@RunWith(SpringRunner.class)
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;

    @Test
    public void insertUserTest(){
        User user = new User();
        user.setPassword("password");
        user.setIntroduction("introduction");
        user.setIconUrl("icon_url");
        user.setRole(Role.ADMIN);
        user.setVerifyCode("verify_code");
        for(int i = 1; i < 10; i++){
            user.setGender(i % 2 == 1);
            user.setIsValid(i % 2 == 1);
            user.setUsername("user" + i);
            user.setEmail("user" + i + "@qq.com");
            user.setPhone("" + i * 1000000);
            userMapper.insertUser(user);
        }
    }

    @Test
    public void updateUserByIdTest(){
        User user = new User();

        //先测试有选择性的更新操作
        user.setId(9);
        user.setRole(Role.NORMAL);
        user.setEmail("new_email");
        userMapper.updateUserByIdSelectively(user);

        //再测试完全更新操作
        user.setId(8);
        user.setUsername("night");
        user.setPassword("12345678");
        user.setEmail("123@123.com");
        user.setPhone("12345678");
        user.setGender(false);
        user.setRole(Role.FOUNDER);
        user.setIsValid(false);
        user.setVerifyCode("12345678");
        userMapper.updateUserById(user);
    }

    @Test
    public void deleteUserByIdTest(){
        userMapper.deleteUserById(9);
        userMapper.deleteUserById(8);
    }

    /*
     * 查询方法测试比较复杂，因此暂时忽略
     */
}
