package com.laidw.web.component;

import com.laidw.entity.User;
import com.laidw.service.MailService;
import com.laidw.web.tools.HtmlPageHelper;
import com.laidw.web.tools.WebHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

/**
 * 一个专门用于发送邮件的组件
 */
@Component
public class Poster {

    @Autowired
    private MailService mailService;


    /**
     * 获取模板邮件所需要的Map
     * @param request HTTP请求对象
     * @param seg 不包含主机名称的uri，不能以'/'开头
     * @param user 用户信息，至少要有id、验证码和邮箱
     * @return 模板邮件所需要的Map
     */
    private Map<String, Object> getEmailMap(HttpServletRequest request, String seg, User user){
        String projectUrl = WebHelper.getProjectUrl(request, seg);
        String verifyUrl = projectUrl + "verify?verifyCode=" + user.getVerifyCode() + "&user_id=" + user.getId();
        Map<String, Object> map = new HashMap<>();
        map.put("user", user);
        map.put("projectUrl", projectUrl);
        map.put("verifyUrl", verifyUrl);
        return map;
    }

    /**
     * 发送验证邮件，由于模板不能解析@{}表达式，因此只能自己把验证链接拼接起来传递给模板，让模板直接取出
     * @param request HTTP请求对象
     * @param seg 不包含主机名称的uri，不能以'/'开头
     * @param user 用户信息，至少要有id、验证码和邮箱
     * @throws Exception 可能抛出的异常
     */
    public void sendVerifyEmail(HttpServletRequest request, String seg, User user) throws Exception {
        Map<String, Object> map = getEmailMap(request, seg, user);
        mailService.sendTemplateMail(user.getEmail(), "Verify Your Email", HtmlPageHelper.MAIL_VERIFY, map);
    }

    /**
     * 发送找回邮件，由于模板不能解析@{}表达式，因此只能自己把验证链接拼接起来传递给模板，让模板直接取出
     * @param request HTTP请求对象
     * @param seg 不包含主机名称的uri，不能以'/'开头
     * @param user 用户信息，至少要有id、验证码和邮箱
     * @throws Exception 可能抛出的异常
     */
    public void sendFindBackEmail(HttpServletRequest request, String seg, User user) throws Exception {
        Map<String, Object> map = getEmailMap(request, seg, user);
        mailService.sendTemplateMail(user.getEmail(), "Find Back Your Account Info", HtmlPageHelper.MAIL_FIND_BACK, map);
    }
}