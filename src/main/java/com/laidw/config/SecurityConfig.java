package com.laidw.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * SpringSecurity的相关配置
 * 主要是配置登录功能和验证功能
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * 对SpringSecurity进行一些基本的配置
     * @param http 通过调用该对象的方法来进行配置
     * @throws Exception 正常情况下不会抛出异常，可以忽略
     */
    protected void configure(HttpSecurity http) throws Exception{

        /*
         * 配置登录功能
         * 调用loginPage("/login")方法后，登录时将不会使用SpringSecurity提供的默认登录页面
         * - 我们需要自己定义控制器拦截/login[GET]请求，返回自定义的登录页面
         * - 而用户发送/login[POST]请求时，SpringSecurity将自动验证用户是否存在
         * defaultSuccessUrl("/")方法表示登录成功后默认跳转到首页
         * - 如果之前是因为权限不足而跳转到登录页面的，则登录后直接跳转到原来的页面
         */
        http.formLogin().loginPage("/login").defaultSuccessUrl("/");

        /*
         * 配置登录功能
         * failureUrl("/login?loginError")方法表示验证失败时重定向到/login?loginError
         * 失败页面默认是/login?error，将请求参数改成loginError主要是为了更好地区分
         */
        http.formLogin().failureUrl("/login?loginError");

        /*
         * 开启记住我功能
         * 用户勾选记住我选项时，SpringSecurity将自动向浏览器写入一个Cookie，用户下次访问会直接登录
         * Cookie的名称指定为"remember-me"
         */
        http.rememberMe().rememberMeParameter("remember-me");

        /*
         * 开启登出功能
         * 用户发送/logout请求（GET或POST都行）时将会退出登录，回到首页，同时清除掉记住我的Cookie
         */
        http.logout().logoutSuccessUrl("/");

        /*
         * 关闭CSRF功能
         */
        http.csrf().disable();

        /*
         * 允许页面通过使用iframe标签来访问本网站的其它页面
         */
        http.headers().frameOptions().sameOrigin();

        /*
         * 配置访问权限，大部分页面都需要登录后才能访问
         */
        http.authorizeRequests()
                .antMatchers("/search/**", "/user/**", "/group/**", "/membership/**", "/task/**", "/friend/**", "/message/**")
                .authenticated();
    }

    /**
     * 配置账户信息的来源，通过userDetailsService查数据库来获取账户信息
     * @param auth 调用该对象的相关方法即可完成设置
     * @throws Exception 正常情况下这里不会抛出异常，可以忽略
     */
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }
}
