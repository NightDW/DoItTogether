package com.laidw.web.controller;

import com.laidw.entity.Group;
import com.laidw.entity.Membership;
import com.laidw.entity.User;
import com.laidw.entity.common.Role;
import com.laidw.service.GroupService;
import com.laidw.service.MembershipService;
import com.laidw.web.controller.tools.HtmlPageHelper;
import com.sun.org.apache.xpath.internal.operations.Mod;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/group")
public class GroupController extends BaseController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private MembershipController membershipController;

    @Value("${dit.upload-photo-dir}")
    private String tarDir;

    @GetMapping
    public String toCreateGroupPage(){
        return HtmlPageHelper.GROUP_INFO;
    }

    @PostMapping
    public ModelAndView doCreateGroup(Group group){
        ModelAndView mav;
        try{
            groupService.foundGroup(getCurrentUser().getId(), group);
        }catch (Exception e){
            mav = new ModelAndView(HtmlPageHelper.GROUP_INFO);
            mav.addObject("msg", "Failed to create group!");
            mav.addObject("group", group);
            return mav;
        }
        mav = toGroupTalkPage(group.getId(), false);
        mav.addObject("msg", "Created group successfully!");
        return mav;
    }

    @GetMapping("/icon/{gid}")
    public ModelAndView toGroupIconUploadPage(@PathVariable Integer gid){
        ModelAndView mav = new ModelAndView(HtmlPageHelper.ICON);
        mav.addObject("url", "group/icon/" + gid);
        return mav;
    }

    @PostMapping("/icon/{gid}")
    public ModelAndView doUserIconUpload(MultipartFile icon, HttpServletRequest request, @PathVariable Integer gid) {
        ModelAndView mav;

        //如果用户没上传图片或上传的文件不是图片，则把错误信息告知用户并转发回上传头像页面
        String fileType = icon.getContentType();
        if (icon.getSize() == 0 || fileType == null || !fileType.startsWith("image/")) {
            mav = toGroupIconUploadPage(gid);
            mav.addObject("msg", "Please upload an image!");
            return mav;
        }
        //如果用户上传了图片，则获取图片的名称（主要是为了获取到它的后缀名），并生成唯一的文件名
        String filename = icon.getOriginalFilename();
        filename = generateVerifyCode() + filename.substring(filename.lastIndexOf('.'));

        //保存文件
        try {
            icon.transferTo(new File(tarDir + "group", filename));
        }
        //如果图片保存失败，则把错误信息告知用户并转发回上传头像页面
        catch (Exception e) {
            mav = toGroupIconUploadPage(gid);
            mav.addObject("msg", "Failed to save image!");
            return mav;
        }
        //如果图片上传成功，则修改数据库数据
        Group group = new Group(gid);
        group.setIconUrl("group/" + filename);
        groupService.updateGroupByIdSelectively(group);

        mav = toGroupTalkPage(gid, false);
        mav.addObject("msg", "Changed Successfully!");
        return mav;
    }

    @GetMapping("/join/{gid}")
    public ModelAndView doJoinGroup(@PathVariable Integer gid){
        Integer userId = getCurrentUser().getId();
        Membership membership = membershipService.selectMembership(userId, gid, true);
        if(membership != null){
            ModelAndView mav = toGroupTalkPage(gid, false);
            mav.addObject("msg", "You had joined in this group!");
            return mav;
        }
        membershipService.joinGroup(userId, gid, Role.NORMAL, null);
        ModelAndView mav = toGroupTalkPage(gid, false);
        mav.addObject("msg", "Joined successfully!");
        return mav;
    }

    @DeleteMapping("/leave/{gid}")
    public ModelAndView doLeaveGroup(@PathVariable Integer gid){
        ModelAndView mav;
        membershipService.leaveGroup(getCurrentUser().getId(), gid);
        mav = membershipController.toGroupListPage();
        mav.addObject("msg", "Left successfully!");
        return mav;
    }

    @GetMapping("/{gid}")
    public ModelAndView toGroupTalkPage(@PathVariable Integer gid,@RequestParam(defaultValue="false") Boolean goToTasks){
        ModelAndView mav = new ModelAndView(HtmlPageHelper.GROUP_TALK);
        mav.addObject("group", groupService.selectGroupById(gid, false));
        Membership membership = membershipService.selectMembership(getCurrentUser().getId(), gid, false);
        mav.addObject("myMembership", membership);
        mav.addObject("isAdmin", membership.getRole().equals(Role.ADMIN) || membership.getRole().equals(Role.FOUNDER));
        mav.addObject("isFounder", membership.getRole().equals(Role.FOUNDER));
        mav.addObject("goToTasks", goToTasks);
        return mav;
    }

    @DeleteMapping
    public ModelAndView doDeleteGroup(Integer id){
        ModelAndView mav;
        Membership membership = membershipService.selectMembership(getCurrentUser().getId(), id, true);
        if(membership == null || !membership.getRole().equals(Role.FOUNDER)){
            mav = membershipController.toGroupListPage();
            mav.addObject("msg", "You have no right to delete this group!");
            return mav;
        }
        groupService.deleteGroupById(id);
        mav = membershipController.toGroupListPage();
        mav.addObject("msg", "Deleted successfully!");
        return mav;
    }

    @PutMapping
    @ResponseBody
    public String doUpdateGroup(Group group){
        groupService.updateGroupByIdSelectively(group);
        return "OK";
    }
}
