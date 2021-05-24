package com.laidw.entity;

import com.laidw.entity.common.Role;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

/**
 * 用于保存DIT平台上注册的账户的信息
 * 该类实现了UserDetails接口，以让SpringSecurity也能够获取到用户信息
 */
@Getter@Setter
@NoArgsConstructor
public class User implements UserDetails {

    /**
     * 账户的id
     */
    private Integer id;

    /**
     * 账户的用户名（不允许重复）
     */
    private String username;

    /**
     * 账户的密码
     */
    private String password;

    /**
     * 用户的个性签名/自我介绍
     */
    private String introduction;

    /**
     * 账户的头像地址
     */
    private String iconUrl;

    /**
     * 账户绑定的邮箱
     */
    private String email;

    /**
     * 账户的电话（暂时不严格要求，因此该属性可以随意填写）
     */
    private String phone;

    /**
     * 账户的性别，true表示男，false表示女
     */
    private Boolean gender;

    /**
     * 账户的角色
     */
    private Role role;

    /**
     * 账户是否激活
     */
    private Boolean isValid;

    /**
     * 激活账户时使用的验证码
     */
    private String verifyCode;

    /**
     * 该用户的好友关系
     */
    private List<Friendship> friendships;

    /**
     * 该用户加入（包括自己创建）的群，以及在该群中的角色等信息
     */
    private List<Membership> memberships;

    /**
     * 该用户接收的任务；注意我们不需要过多地关心该用户发布的任务
     * 必要时只需要在任务的详细信息中展示发布者的信息即可
     */
    private List<Task> acceptedTasks;

    public User(Integer id) {
        this.id = id;
    }

    /**
     * SpringSecurity将通过该方法获取用户的权限信息；由于本工程不涉及复杂的权限，因此这里返回该用户的角色即可，注意角色需要以"ROLE_"开头
     * @return 封装后的用户的身份信息
     */
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> authorities = new HashSet<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.role.name()));
        return authorities;
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isAccountNonLocked() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    public boolean isEnabled() {
        return this.isValid;
    }
}