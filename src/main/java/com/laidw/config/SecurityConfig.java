package com.laidw.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * SpringSecurity的相关配置
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    protected void configure(HttpSecurity http) throws Exception{

        //开启登录功能
        //使用我们自定义的登录页面而不是SpringBoot提供的默认登录页面
        //用户用POST方式发送/login请求时SpringSecurity自动验证用户是否存在
        //登录成功则跳到首页，失败则重定向到/login?loginError
        //（失败页面默认是/login?error，改成loginError主要为了更好地区分）
        http.formLogin().loginPage("/login").defaultSuccessUrl("/");
        http.formLogin().failureUrl("/login?loginError");

        //开启记住我功能，用户勾选记住我后SpringSecurity自动向浏览器写入一个Cookie，用户下次访问会直接登录
        http.rememberMe().rememberMeParameter("remember-me");

        //开启登出功能，用户发送/logout请求时将会退出登录，回到首页，同时清除掉记住我的Cookie
        http.logout().logoutSuccessUrl("/");

        //关闭CSRF功能
        http.csrf().disable();

        //允许页面使用iframe标签访问本网站的页面
        http.headers().frameOptions().sameOrigin();

        //配置访问权限
        http.authorizeRequests()
                .antMatchers("/").permitAll()
                .antMatchers("/search/**", "/user/**", "/group/**", "/membership/**", "/task/**", "/friend/**", "/message/**").authenticated();
    }

    /**
     * 进行相关的配置
     * @param auth 调用该对象的相关方法即可完成设置
     * @throws Exception 这里不会抛出异常，不用管
     */
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }
}
