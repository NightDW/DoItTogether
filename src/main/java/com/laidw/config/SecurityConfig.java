package com.laidw.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

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
         * loginPage("/login")作用：不使用SpringSecurity提供的默认登录页面
         *   - 因此我们需要自己定义控制器来拦截/login[GET]请求，并返回自定义的登录页面
         *   - 另外，当用户发送/login[POST]请求时，SpringSecurity将自动验证用户是否存在
         * defaultSuccessUrl("/")作用：用于指定登录成功后默认跳转到首页
         *   - 但是，如果之前是因为权限不足而跳转到登录页面的，则登录后直接跳转到之前请求的页面
         */
        http.formLogin().loginPage("/login").defaultSuccessUrl("/");

        /*
         * 配置登录功能
         * failureUrl("/login?loginError")作用：用于指定验证失败时重定向到/login?loginError（默认是/login?error）
         * 这里我们将登录失败时的请求参数改成loginError，主要是为了更好地辨别错误的类型
         */
        http.formLogin().failureUrl("/login?loginError");

        /*
         * 开启记住我功能
         * 开启后，如果用户登录时勾选了"记住我"选项，那么SpringSecurity将向浏览器写入一个Cookie，这样用户下次访问时就会自动登录
         * 通过rememberMeParameter("remember-me")方法来指定这个Cookie的名称为"remember-me"
         */
        http.rememberMe().rememberMeParameter("remember-me");

        /*
         * 开启登出功能
         * 开启后，用户发送/logout请求（GET或POST都行）时将会退出登录，回到首页，同时清除掉记住我的Cookie
         */
        http.logout().logoutSuccessUrl("/");

        /*
         * 关闭CSRF功能
         */
        http.csrf().disable();

        /*
         * 允许本网站的页面通过使用iframe标签来访问本网站的其它页面
         */
        http.headers().frameOptions().sameOrigin();

        /*
         * 配置访问权限：除了注册和登录等页面，其它大部分页面都需要登录后才能访问
         */
        http.authorizeRequests()
                .antMatchers("/search/**", "/user/**", "/group/**", "/membership/**", "/task/**", "/friend/**", "/message/**")
                .authenticated();
    }

    /**
     * 配置账户信息的来源；账户信息存放在数据库中，因此需要通过userDetailsService来查数据库以获取账户信息
     * @param auth 调用该对象的相关方法即可完成设置
     * @throws Exception 正常情况下这里不会抛出异常，可以忽略
     */
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService);
    }

    /**
     * 高版本的SpringSecurity要求我们提供一个密码解析器（直接将密码解析器放到IOC容器中即可）
     * @return 本项目没有对密码进行加密，因此返回一个简单的解析器即可
     */
    @Bean
    public PasswordEncoder getPasswordEncoder(){
        return new PasswordEncoder() {

            public String encode(CharSequence charSequence) {
                return charSequence.toString();
            }

            public boolean matches(CharSequence charSequence, String s) {
                return s.equals(charSequence.toString());
            }

        };
    }
}