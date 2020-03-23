package com.laidw.web.controller;

import com.laidw.entity.User;
import com.laidw.service.FriendshipService;
import com.laidw.service.MailService;
import com.laidw.service.UserService;
import com.laidw.web.controller.tools.HtmlPageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

@Controller
@RequestMapping("/user")
public class UserController extends BaseController {

    @Autowired
    private UserService userService;

    @Autowired
    private String imgDir;

    @GetMapping
    public ModelAndView toModifyMyInfoPage(){
        ModelAndView mav = new ModelAndView(HtmlPageHelper.USER_INFO);
        mav.addObject("user", getCurrentUser());
        mav.addObject("title", "Modify");
        return mav;
    }

    @PutMapping
    public ModelAndView doModifyMyInfo(User user, HttpServletRequest request){
        ModelAndView mav = new ModelAndView();

        //先检测用户是否更改绑定邮箱
        user.setId(getCurrentUser().getId());
        boolean changeEmail = !(getCurrentUser().getEmail().equals(user.getEmail()));
        String verifyCode = null;

        //如果改变了绑定邮箱，则需锁定该账户，生成新的验证码，让用户重新激活
        if(changeEmail){
            user.setIsValid(false);
            verifyCode = generateVerifyCode();
            user.setVerifyCode(verifyCode);
        }
        //不管有没有改变绑定邮箱，都先尝试把用户提交的信息保存到数据库（用户提交的数据不一定是合法的）
        try {
            userService.updateUserById(user, true);
        } catch (Exception e) {
            //如果修改失败，则转发回修改页面
            mav.addObject("msg", "Failed to modify! Check your input please!");
            mav.addObject("user", user);
            mav.addObject("title", "Modify");
            mav.setViewName(HtmlPageHelper.USER_INFO);
            return mav;
        }
        //如果修改成功，且改变了绑定的邮箱，则发送验证邮件，并自动登出
        if(changeEmail){
            mav.setViewName(HtmlPageHelper.LOGOUT_SOON);
            try {
                sendVerifyEmail(request, "user", verifyCode, user.getId(), user.getEmail());
            } catch (Exception e) {
                mav.addObject("msg", "You changed your email but we failed to send validate link to your new email! Logging out...");
                return mav;
            }
            mav.addObject("msg", "Modified successfully! But you need to verify your new email before using your account! Logging out...");
            return mav;
        }
        //如果修改成功，且没有更改绑定的邮箱，此时可以自动重新登录
        //只需要转发到登录页面，同时指定auto_login为true，并把用户数据传过去即可
        mav.addObject("user", userService.selectUserByCondition(user, true));
        mav.addObject("auto_login", true);
        mav.setViewName(HtmlPageHelper.LOGIN);
        return mav;
    }

    @GetMapping("/icon")
    public ModelAndView toUserIconUploadPage(){
        ModelAndView mav = new ModelAndView(HtmlPageHelper.ICON);
        mav.addObject("url", "user/icon");
        return mav;
    }

    @PostMapping("/icon")
    public ModelAndView doUserIconUpload(MultipartFile icon, HttpServletRequest request) {
        ModelAndView mav;

        //如果用户没上传图片或上传的文件不是图片，则把错误信息告知用户并转发回上传头像页面
        String fileType = icon.getContentType();
        if (icon.getSize() == 0 || fileType == null || !fileType.startsWith("image/")) {
            mav = toUserIconUploadPage();
            mav.addObject("msg", "Please upload an image!");
            return mav;
        }
        //如果用户上传了图片，则获取图片的名称（主要是为了获取到它的后缀名），并生成唯一的文件名
        String filename = icon.getOriginalFilename();
        filename = generateVerifyCode() + filename.substring(filename.lastIndexOf('.'));

        //保存文件
        try {
            icon.transferTo(new File(imgDir + "user", filename));
        }
        //如果图片保存失败，则把错误信息告知用户并转发回上传头像页面
        catch (Exception e) {
            mav = toUserIconUploadPage();
            mav.addObject("msg", "Failed to save image!");
            return mav;
        }
        //如果图片上传成功，则获取用户id，用于修改数据库数据
        User user = new User(getCurrentUser().getId());
        user.setIconUrl("user/" + filename);
        userService.updateUserById(user, true);

        //更新后查出该用户的新数据，转发到登录页面并自动登录
        mav = new ModelAndView(HtmlPageHelper.LOGIN);
        mav.addObject("user", userService.selectUserByCondition(user, true));
        mav.addObject("auto_login", true);
        return mav;
    }

    @GetMapping("/{id}")
    public ModelAndView toUserInfoPage(@PathVariable Integer id){
        ModelAndView mav = new ModelAndView(HtmlPageHelper.USER_INFO);
        mav.addObject("user", userService.selectUserByCondition(new User(id), true));
        mav.addObject("readOnly", true);
        mav.addObject("title", "User Detail");
        return mav;
    }
}
