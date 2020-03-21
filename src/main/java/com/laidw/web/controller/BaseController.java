package com.laidw.web.controller;

import com.laidw.entity.User;
import com.laidw.entity.common.Role;
import com.laidw.service.GroupService;
import com.laidw.service.MailService;
import com.laidw.service.UserService;
import com.laidw.web.controller.tools.HtmlPageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * 主要处理一些允许匿名访问的请求，同时提供一些方法供其它Controller调用
 */
@Controller
public class BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private MailService mailService;

    @GetMapping({"/", "/index"})
    public String toHomePage(){
        return HtmlPageHelper.INDEX;
    }

    @GetMapping("/login")
    public String toLoginPage(){
        return HtmlPageHelper.LOGIN;
    }

    @GetMapping("/logout-soon")
    public String toLogoutSoonPage(){
        return HtmlPageHelper.LOGOUT_SOON;
    }

    @GetMapping("/register")
    public ModelAndView toRegisterPage(){
        ModelAndView mav = new ModelAndView(HtmlPageHelper.USER_INFO);
        mav.addObject("title", "Register");
        return mav;
    }

    @PostMapping("/register")
    public ModelAndView doRegister(User user, HttpServletRequest request){
        //设置默认属性
        String verifyCode = generateVerifyCode();
        user.setRole(Role.NORMAL);
        user.setIsValid(false);
        user.setVerifyCode(verifyCode);

        //不管是否成功注册，都需要把用户已填写的信息传给要转发到的页面
        ModelAndView mav = new ModelAndView();
        mav.addObject("user", user);

        try{
            userService.insertUser(user);
        } catch (Exception e){
            //注册失败则回到注册页面
            mav.setViewName(HtmlPageHelper.USER_INFO);
            mav.addObject("msg", "Failed to register! Please check your inputs!");
            mav.addObject("title", "Register");
            return mav;
        }
        //插入成功则发送模板邮件；不管邮件是否发送成功，用户数据都已经添加到了数据库中，区别只是用户收到验证邮件与否
        //因此固定转发到登录页面，并且把此次注册的用户数据传给登录页面，注意此时账户尚未激活
        mav.setViewName(HtmlPageHelper.LOGIN);
        try {
            sendVerifyEmail(request, "register", verifyCode, user.getId(), user.getEmail());
        } catch (Exception e) {
            //如果发送失败，则还需要告知用户错误信息
            mav.addObject("msg", "Registered successfully! But failed to send verifying email!");
            return mav;
        }
        //如果发送成功，则还需要告知用户前去查看邮件
        mav.addObject("msg", "Registered successfully! But your account should be validated before using!");
        return mav;
    }

    @GetMapping("/forget")
    public String toForgetPage(){
        return HtmlPageHelper.FORGET;
    }

    @PostMapping("/forget")
    public ModelAndView doForget(@RequestParam(required=false) String username, @RequestParam(required=false) String email, @RequestParam(required=false) String phone, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();

        //注意，username,phone,email这3个参数必定不为空，原因不明，可能是因为"required=false"
        //如果用户未提交任何数据，则转发回forget页面，并提示错误信息，这一步基本不会执行，因为页面有JS代码检查输入是否为空
        if (username.isEmpty() && phone.isEmpty() && email.isEmpty()) {
            mav.addObject("msg", "Please provide your username/email/phone!");
            mav.setViewName(HtmlPageHelper.FORGET);
            return mav;
        }

        User user = new User();
        user.setUsername(username.isEmpty() ? null : username);
        user.setEmail(email.isEmpty() ? null : email);
        user.setPhone(phone.isEmpty() ? null : phone);
        User trueUser = userService.selectUserByCondition(user, true);

        //如果根据用户指定的信息在数据库中找不到对应的用户数据，则转发回forget页面，并提示错误信息
        if (trueUser == null) {
            mav.addObject("msg", "Cannot find your account! Please check the info you input!");
            mav.addObject("user", user);
            mav.setViewName(HtmlPageHelper.FORGET);
            return mav;
        }

        //如果找到了，则向找到的用户的邮箱发送邮件
        try {
            sendFindBackEmail(request, "forget", trueUser);
        } catch (Exception e) {
            //如果发送失败，则转发回找回页面
            mav.addObject("msg", "Error occurred when sending email! Please try again!");
            mav.addObject("user", user);
            mav.setViewName(HtmlPageHelper.FORGET);
            return mav;
        }
        //如果发送成功，则转发到登录页面，注意不能把查到的用户数据传给登录页面，防止A通过找回功能获取到B账号的密码
        mav.addObject("msg", "Sent your account info to your email successfully!");
        mav.setViewName(HtmlPageHelper.LOGIN);
        return mav;
    }

    //如果相关的数据未被注册，则返回空；否则返回错误信息
    @GetMapping("/check")
    @ResponseBody
    public String doCheck(String condition, String id){
        //注意，这里的condition是"username_laidw"格式的
        int splitIndex = condition.indexOf('_');
        String attName = condition.substring(0, splitIndex);
        String attValue = condition.substring(splitIndex + 1);

        //利用反射来设置相关属性
        User user = new User();
        try {
            Class clazz = Class.forName("com.laidw.entity.User");
            Field field = clazz.getDeclaredField(attName);
            field.setAccessible(true);
            field.set(user, attValue);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        user = userService.selectUserByCondition(user, true);

        return user == null || user.getId().toString().equals(id) ? null : attName + " had been registered!";
    }

    @GetMapping("/verify")
    public ModelAndView doVerify(String verifyCode, Integer user_id) {
        //不管验证是否成功，都会转发到登录页面
        ModelAndView mav = new ModelAndView(HtmlPageHelper.LOGIN);

        //取出真正的验证码，和用户提交的验证码比较
        User user = userService.selectUserByCondition(new User(user_id), true);
        String trueCode = user.getVerifyCode();

        //验证成功则激活该账户，把账户信息转发到登录页面，否则提示失败信息
        if (trueCode.equals(verifyCode)) {
            User tem = new User(user_id);
            tem.setIsValid(true);
            userService.updateUserById(tem, true);
            mav.addObject("msg", "Your account has been validated successfully!");
            mav.addObject("user", user);
        } else
            mav.addObject("msg", "Failed to validate your account!");
        return mav;
    }

    @PostMapping("/search")
    public ModelAndView doSearch(Boolean rough, String key){
        Boolean isRough = rough != null && rough;
        ModelAndView mav = new ModelAndView(HtmlPageHelper.SEARCH);
        mav.addObject("users", userService.searchUsers(key, isRough));
        mav.addObject("groups", groupService.searchGroups(key, isRough));
        mav.addObject("key", key);
        mav.addObject("rough", isRough);
        return mav;
    }

    //-----------------------------------------------------------------------------------------------------------------------------------------

    //模板不能解析@{}表达式，只能自己把验证链接拼接起来传递给模板，让模板直接取出
    protected void sendVerifyEmail(HttpServletRequest request, String seg, String verifyCode, Integer uid, String userEmail) throws Exception {
        String requestUrl = request.getRequestURL().toString();
        String projectUrl = requestUrl.substring(0, requestUrl.lastIndexOf(seg));
        String verifyUrl = projectUrl + "verify?verifyCode=" + verifyCode + "&user_id=" + uid;
        Map<String, Object> map = new HashMap<>();
        map.put("projectUrl", projectUrl);
        map.put("verifyUrl", verifyUrl);
        mailService.sendTemplateMail(userEmail, "Verify Your Email", HtmlPageHelper.MAIL_VERIFY, map);
    }

    protected void sendFindBackEmail(HttpServletRequest request, String seg, User trueUser) throws Exception {
        String requestUrl = request.getRequestURL().toString();
        String projectUrl = requestUrl.substring(0, requestUrl.lastIndexOf(seg));
        String verifyUrl = projectUrl + "verify?verifyCode=" + trueUser.getVerifyCode() + "&user_id=" + trueUser.getId();
        Map<String, Object> map = new HashMap<>();
        map.put("user", trueUser);
        map.put("verifyUrl", verifyUrl);
        mailService.sendTemplateMail(trueUser.getEmail(), "Find Back Your Account Info", HtmlPageHelper.MAIL_FIND_BACK, map);
    }

    protected String generateVerifyCode() {
        return UUID.randomUUID().toString();
    }

    protected User getCurrentUser(){
        return (User)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    }
}
