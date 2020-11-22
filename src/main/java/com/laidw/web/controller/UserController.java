package com.laidw.web.controller;

import com.laidw.entity.User;
import com.laidw.service.UserService;
import com.laidw.web.component.Poster;
import com.laidw.web.tools.HtmlPageHelper;
import com.laidw.web.tools.WebHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;

/**
 * 负责处理用户相关的业务
 */
@Controller
@RequestMapping("/user")
public class UserController{

    @Autowired
    private UserService userService;

    @Autowired
    private Poster poster;


    /**
     * 前往修改用户信息页面
     * @return 修改用户信息页面
     */
    @GetMapping
    public ModelAndView toModifyMyInfoPage(){
        ModelAndView mav = new ModelAndView(HtmlPageHelper.USER_INFO);
        mav.addObject("user", WebHelper.getCurrentUser());
        mav.addObject("title", "Modify");
        return mav;
    }

    /**
     * 处理用户的修改个人信息的请求
     * @param user 用户提交数据
     * @param request HTTP请求对象
     * @return 修改了邮箱，则退出登录等待重新验证邮箱；否则直接重新登录以刷新数据
     */
    @PutMapping
    public ModelAndView doModifyMyInfo(User user, HttpServletRequest request){
        ModelAndView mav = new ModelAndView();

        //给用户提交的数据设置id
        User currentUser = WebHelper.getCurrentUser();
        user.setId(currentUser.getId());

        //检测用户是否更改绑定邮箱
        boolean changeEmail = !(currentUser.getEmail().equals(user.getEmail()));

        //如果改变了绑定邮箱，则需锁定该账户，生成新的验证码，让用户重新激活
        if(changeEmail){
            user.setIsValid(false);
            user.setVerifyCode(WebHelper.generateVerifyCode());
        }

        //尝试更新用户信息（用户提交的数据不一定是合法的）
        try {
            userService.updateUserById(user, true);

        //如果更新失败，则回到修改信息页面
        } catch (Exception e) {
            mav.addObject("msg", "Failed to modify! Check your input please!");
            mav.addObject("user", user);
            mav.addObject("title", "Modify");
            mav.setViewName(HtmlPageHelper.USER_INFO);
            return mav;
        }

        //如果修改成功，且没有更改绑定的邮箱，此时可以自动重新登录
        //只需要转发到登录页面，同时指定auto_login为true，并把用户数据传过去即可
        if(!changeEmail){
            mav.addObject("user", userService.selectUserByCondition(user, true));
            mav.addObject("auto_login", true);
            mav.setViewName(HtmlPageHelper.LOGIN);
            return mav;
        }

        //如果修改成功，且改变了绑定的邮箱，则发送验证邮件，并自动登出
        mav.setViewName(HtmlPageHelper.LOGOUT_SOON);
        try {
            poster.sendVerifyEmail(request, "user", user);
        } catch (Exception e) {
            mav.addObject("msg", "You changed your email but we failed to send validate link to your new email! Logging out...");
            return mav;
        }
        mav.addObject("msg", "Modified successfully! But you need to verify your new email before using your account! Logging out...");
        return mav;
    }

    /**
     * 查看某个用户的公开信息
     * @param id 用于的id
     * @return 用户详细信息页面
     */
    @GetMapping("/{id}")
    public ModelAndView toUserInfoPage(@PathVariable Integer id){
        ModelAndView mav = new ModelAndView(HtmlPageHelper.USER_INFO);
        mav.addObject("user", userService.selectUserByCondition(new User(id), true));
        mav.addObject("readOnly", true);
        mav.addObject("title", "User Detail");
        return mav;
    }
}