package com.laidw.web.controller;

import com.laidw.entity.Group;
import com.laidw.entity.Membership;
import com.laidw.entity.User;
import com.laidw.entity.common.Role;
import com.laidw.service.GroupService;
import com.laidw.service.MembershipService;
import com.laidw.service.UserService;
import com.laidw.web.tools.HtmlPageHelper;
import com.laidw.web.tools.WebHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;

/**
 * 主要负责处理用户/群组头像上传的业务
 * 这两个功能的代码重复度较高，因此定义该Controller来统一处理
 * 群组头像相关的url为/icon/group/{gid}，用户头像相关的url为/icon/user/{xxx}
 */
@Controller
@RequestMapping("/icon")
public class ImageController {

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private UserService userService;

    @Autowired
    private GroupService groupService;

    @Autowired
    private GroupController groupController;

    @Autowired
    private String imgDir;


    /**
     * 前往头像上传页面
     * @param type 表示要上传用户头像还是群组头像
     * @param gid 要修改头像的群组的id，如果是修改用户头像，则可以忽略该参数
     * @param msg 携带的消息，该参数主要由控制器的其它方法提供
     * @return 头像上传页面
     */
    @GetMapping("/{type}/{gid}")
    public ModelAndView toIconUploadPage(@PathVariable String type, @PathVariable Integer gid, @RequestParam(required=false) String msg){
        ModelAndView mav = new ModelAndView(HtmlPageHelper.ICON);

        //通过url属性来区分是上传用户头像还是群组头像
        mav.addObject("url", "icon/" + type + "/" + gid);
        mav.addObject("msg", msg);
        return mav;
    }

    /**
     * 负责处理用户上传图片的请求
     * @param type 表示用户上传的是用户头像还是群组头像
     * @param gid 群组的id，如果是上传用户头像，则可以忽略该参数
     * @param icon 用户上传的图片
     * @param request HTTP请求对象
     * @return 如果上传失败，则回到上传页面；如果上传成功，则回到群组详细信息页面或自动登录页面
     */
    @PostMapping("/{type}/{gid}")
    public ModelAndView doGroupIconUpload(@PathVariable String type, @PathVariable Integer gid, MultipartFile icon, HttpServletRequest request) {

        //如果用户没上传文件或文件没有后缀名或上传的文件不是图片，则把错误信息告知用户并转发回上传头像页面
        String filename = icon.getOriginalFilename();
        String fileType = icon.getContentType();
        if (icon.getSize() == 0 || filename == null || filename.lastIndexOf('.') == -1 || fileType == null || !fileType.startsWith("image/"))
            return toIconUploadPage(type, gid, "Please upload an image!");

        //如果是修改群组头像，则先判断用户是否有权限
        if("group".equals(type)){
            Membership membership = membershipService.selectMembershipByUG(WebHelper.getCurrentUser().getId(), gid, true);

            //如果用户未加入群组，或者只是普通群成员，则调用GroupController的toGroupTalkPage()方法
            //该方法会判断用户是否加入群组，如果没有，则返回群组列表页面；如果有，则会转发会群组详细信息页面并显示msg消息
            if(membership == null || Role.NORMAL.equals(membership.getRole()))
                return groupController.toGroupTalkPage(gid, false, "You have no right to change the icon of this group!");
        }

        //为图片生成一个唯一的文件名
        filename = WebHelper.generateUUID() + filename.substring(filename.lastIndexOf('.'));

        try {
            icon.transferTo(new File(imgDir + '/' + type, filename));
        } catch (Exception e) {

            //如果图片保存失败，则把错误信息告知用户并转发回上传头像页面
            return toIconUploadPage(type, gid, "Failed to save image! Reason: " + e.getMessage());
        }

        //如果图片上传成功，则根据修改的是用户头像还是群组头像来决定下一步怎么做
        return "group".equals(type) ? doGroup(filename, gid) : doUser(filename);
    }

    private ModelAndView doGroup(String filename, Integer gid) {
        Group group = new Group(gid);
        group.setIconUrl("group/" + filename);
        groupService.updateGroupByIdSelectively(group);
        return groupController.toGroupTalkPage(gid, false, "Changed Successfully!");
    }

    private ModelAndView doUser(String filename) {
        User user = new User(WebHelper.getCurrentUser().getId());
        user.setIconUrl("user/" + filename);
        userService.updateUserById(user, true);

        //更新后查出该用户的新数据，转发到登录页面并开启自动登录
        ModelAndView mav = new ModelAndView(HtmlPageHelper.LOGIN);
        mav.addObject("user", userService.selectUserByCondition(user, true));
        mav.addObject("auto_login", true);
        return mav;
    }
}