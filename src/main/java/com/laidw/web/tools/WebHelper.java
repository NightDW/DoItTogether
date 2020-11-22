package com.laidw.web.tools;

import com.laidw.entity.User;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.util.UUID;

/**
 * 一个工具类，用于获取项目的根url，生成UUID，获取当前登录的用户等
 */
public class WebHelper {

    private static String PROJECT_URL = null;

    /**
     * 用于获取项目的根url，需要额外提供两个参数，以便在PROJECT_URL为空时初始化它
     * 这种懒汉式加载不会有线程安全问题，因为PROJECT_URL其实是一个常量
     * @param request HTTP请求对象
     * @param seg 不包含主机名称的uri，不能以'/'开头
     * @return 项目的根url
     */
    public static String getProjectUrl(HttpServletRequest request, String seg){
        if(PROJECT_URL == null){
            StringBuffer requestUrl = request.getRequestURL();
            PROJECT_URL = requestUrl.substring(0, requestUrl.lastIndexOf(seg));
        }
        return PROJECT_URL;
    }

    /**
     * 获取验证码
     * @return 返回一个UUID
     */
    public static String generateVerifyCode() {
        return generateUUID();
    }

    /**
     * 获取UUID
     * @return 返回一个UUID
     */
    public static String generateUUID(){
        return UUID.randomUUID().toString();
    }

    /**
     * 获取当前登录的用户
     * @return 当前登录的用户
     */
    public static User getCurrentUser(){
        return (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
