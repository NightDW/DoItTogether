package com.laidw.web.controller;

import com.laidw.entity.User;
import com.laidw.entity.common.Role;
import com.laidw.service.GroupService;
import com.laidw.service.UserService;
import com.laidw.web.component.Poster;
import com.laidw.web.tools.HtmlPageHelper;
import com.laidw.web.tools.WebHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Field;

/**
 * 主要处理一些允许匿名访问的请求
 */
@Controller
public class BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private Poster poster;


    @GetMapping({"/", "/index"})
    public String toIndexPage(){
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

    /**
     * 转发到注册页面，注意USER_INFO页面既可以作为注册页面，也可以作为修改个人信息的页面
     * 因此不能直接转发到该页面，需要再添加一些信息，便于区分
     * @return USER_INFO页面和注册页面所需要的一些数据
     */
    @GetMapping("/register")
    public ModelAndView toRegisterPage(){
        ModelAndView mav = new ModelAndView(HtmlPageHelper.USER_INFO);
        mav.addObject("title", "Register");
        return mav;
    }

    /**
     * 负责处理用户的注册请求
     * @param user 用户提交的信息
     * @param request HTTP请求对象
     * @return 注册成功则转到登录页面，失败则转到注册页面
     */
    @PostMapping("/register")
    public ModelAndView doRegister(User user, HttpServletRequest request){

        //设置默认属性
        String verifyCode = WebHelper.generateVerifyCode();
        user.setRole(Role.NORMAL);
        user.setIsValid(false);
        user.setVerifyCode(verifyCode);

        //不管是否成功注册，都需要把用户已填写的信息传给要转发到目标页面
        ModelAndView mav = new ModelAndView();
        mav.addObject("user", user);

        try{
            userService.insertUser(user);
        } catch (Exception e){

            //注册失败则回到注册页面，并提示错误信息
            mav.setViewName(HtmlPageHelper.USER_INFO);
            mav.addObject("msg", "Failed to register! Please check your inputs!");
            mav.addObject("title", "Register");
            return mav;
        }

        //插入成功则发送验证邮件
        //注意，不管邮件是否发送成功，用户数据都已经添加到了数据库中，区别只是用户收到验证邮件与否
        //因此肯定要转发到登录页面，并且把此次注册的用户数据传给登录页面，注意此时账户尚未激活
        mav.setViewName(HtmlPageHelper.LOGIN);
        try {
            poster.sendVerifyEmail(request, "register", user);
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

    /**
     * 负责处理处理用户的找回账号的请求
     * @param username 用户提交的用户名
     * @param email 用户提交的邮箱
     * @param phone 用户提交的电话号码
     * @param request HTTP请求对象
     * @return 成功则转发到登录页面，失败则转发到forget页面
     */
    @PostMapping("/forget")
    public ModelAndView doForget(@RequestParam(required=false) String username, @RequestParam(required=false) String email, @RequestParam(required=false) String phone, HttpServletRequest request) {
        ModelAndView mav = new ModelAndView();

        //注意，username phone email这3个参数必定不为空，原因不明，可能是因为"required=false"
        //如果用户未提交任何数据，则转发回forget页面，并提示错误信息，这一步基本不会执行，因为页面有JS代码检查输入是否为空
        if (username.isEmpty() && phone.isEmpty() && email.isEmpty()) {
            mav.addObject("msg", "Please provide your username/email/phone!");
            mav.setViewName(HtmlPageHelper.FORGET);
            return mav;
        }

        User condition = new User();
        condition.setUsername(username.isEmpty() ? null : username);
        condition.setEmail(email.isEmpty() ? null : email);
        condition.setPhone(phone.isEmpty() ? null : phone);
        User user = userService.selectUserByCondition(condition, true);

        //如果根据用户指定的信息在数据库中找不到对应的用户数据，则转发回forget页面，并提示错误信息
        if (user == null) {
            mav.addObject("msg", "Cannot find your account! Please check the info you input!");
            mav.addObject("user", condition);
            mav.setViewName(HtmlPageHelper.FORGET);
            return mav;
        }

        //如果找到了，则向找到的用户的邮箱发送邮件
        try {
            poster.sendFindBackEmail(request, "forget", user);
        } catch (Exception e) {

            //如果发送失败，则转发回找回页面
            mav.addObject("msg", "Error occurred when sending email! Please try again!");
            mav.addObject("user", condition);
            mav.setViewName(HtmlPageHelper.FORGET);
            return mav;
        }

        //如果发送成功，则转发到登录页面，注意不能把查到的用户数据传给登录页面，防止A通过找回功能获取到B账号的密码
        mav.addObject("msg", "Sent your account info to your email successfully!");
        mav.setViewName(HtmlPageHelper.LOGIN);
        return mav;
    }

    /**
     * 负责处理浏览器的AJAX请求，检查账户的用户名/邮箱/电话号码等是否已经被其他人注册
     * @param condition 待检测的条件
     * @param id 需要排除掉的id
     * @return 如果相关的数据未被注册，则返回空；否则返回错误信息
     */
    @GetMapping("/check")
    @ResponseBody
    public String doCheck(String condition, String id){

        //注意，这里的condition是"username_laidw"格式的
        //不管提交过来的用户名/邮箱/电话号码是否含有下划线，以第一个下划线作为分隔符总是不会错的
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
            e.printStackTrace();
            return "Error occurred on server!";
        }

        //查询出满足该条件的用户
        user = userService.selectUserByCondition(user, true);

        //如果没查到，或者查到的用户就是本人，说明没被其他人注册，此时返回null，否则返回错误信息
        return user == null || user.getId().toString().equals(id) ? null : attName + " had been registered!";
    }

    /**
     * 负责处理用户的验证请求（用户一般是通过点击验证邮件中的验证链接来发送请求的）
     * @param verifyCode 验证链接中的验证码
     * @param user_id 验证链接中的用户id
     * @return 返回到登录页面
     */
    @GetMapping("/verify")
    public ModelAndView doVerify(String verifyCode, Integer user_id) {

        //不管验证是否成功，都会转发到登录页面
        ModelAndView mav = new ModelAndView(HtmlPageHelper.LOGIN);

        //获取用户的真实信息，如果已经激活，则直接返回
        //注意此时不能把用户真实信息转发到登录页面，避免信息被窃取
        User user = userService.selectUserByCondition(new User(user_id), true);
        if(user.getIsValid()){
            mav.addObject("msg", "Your account had been validated before!");
            return mav;
        }

        //否则，取出真正的验证码，和用户提交的验证码比较
        //相同则激活该账户，并把账户信息转发到登录页面，否则提示失败信息
        if (user.getVerifyCode().equals(verifyCode)) {
            User tem = new User(user_id);
            tem.setIsValid(true);
            userService.updateUserById(tem, true);
            mav.addObject("msg", "Your account has been validated successfully!");
            mav.addObject("user", user);
        } else {
            mav.addObject("msg", "Failed to validate your account!");
        }
        return mav;
    }

    /**
     * 负责处理用户的搜索请求
     * @param rough 是否模糊查询
     * @param key 查询的关键字
     * @return 转发到搜索结果页面
     */
    @PostMapping("/search")
    public ModelAndView doSearch(Boolean rough, String key){
        boolean isRough = (rough != null) && rough;
        ModelAndView mav = new ModelAndView(HtmlPageHelper.SEARCH);
        mav.addObject("users", userService.searchUsers(key, isRough));
        mav.addObject("groups", groupService.searchGroups(key, isRough));
        mav.addObject("key", key);
        mav.addObject("rough", isRough);
        return mav;
    }
}