package com.laidw.web.controller;

import com.laidw.entity.Group;
import com.laidw.entity.Membership;
import com.laidw.entity.common.Role;
import com.laidw.service.GroupService;
import com.laidw.service.MembershipService;
import com.laidw.web.tools.HtmlPageHelper;
import com.laidw.web.tools.WebHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

/**
 * 负责处理群组相关的业务
 */
@Controller
@RequestMapping("/group")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private MembershipController membershipController;


    /**
     * 返回创建群组的页面
     * @return 群组信息的编辑页面
     */
    @GetMapping
    public String toCreateGroupPage(){
        return HtmlPageHelper.GROUP_INFO;
    }

    /**
     * 负责处理用户的创建群组的请求
     * @param group 用户提交的群组信息
     * @return 成功则转发到该群组的详细信息页面，否则跳转回创建群组页面
     */
    @PostMapping
    public ModelAndView doCreateGroup(Group group){
        try{
            groupService.foundGroup(WebHelper.getCurrentUser().getId(), group);
        }catch (Exception e){

            //如果创建群组失败，则返回到创建群组页面
            ModelAndView mav = new ModelAndView(HtmlPageHelper.GROUP_INFO);
            mav.addObject("msg", "Failed to create group! Reason: " + e.getMessage());
            mav.addObject("group", group);
            return mav;
        }

        //创建成功，则来到群组的详细信息页面
        return toGroupTalkPage(group.getId(), false, "Created group successfully!");
    }

    /**
     * 负责处理当前用户加入某个群组的请求
     * @param gid 要加入的群组的id
     * @return 转发到群组的详细信息页面
     */
    @GetMapping("/join/{gid}")
    public ModelAndView doJoinGroup(@PathVariable Integer gid){
        Integer userId = WebHelper.getCurrentUser().getId();
        Membership membership = membershipService.selectMembershipByUG(userId, gid, true);

        if(membership != null)
            return toGroupTalkPage(gid, false, "You had joined in this group!");

        membershipService.joinGroup(userId, gid, Role.NORMAL, null);
        return toGroupTalkPage(gid, false, "Joined in this group successfully!");
    }

    /**
     * 负责处理用户离开群组的请求
     * @param gid 要离开的群组的id
     * @return 回到群组列表页面
     */
    @DeleteMapping("/leave/{gid}")
    public ModelAndView doLeaveGroup(@PathVariable Integer gid){
        membershipService.leaveGroup(WebHelper.getCurrentUser().getId(), gid);
        return membershipController.toGroupListPage("Left successfully!");
    }

    /**
     * 前往群组详细信息页面，该页面有两个标签页：home和tasks，默认打开home页面
     * @param gid 群组的id
     * @param goToTasks 是否直接打开tasks页面
     * @param msg 需要显示的消息，设置该参数主要是为了方便其它控制器方法调用本方法
     * @return 如果用户没有加入该群，则返回群组列表页面，否则进入群组的详细信息页面
     */
    @GetMapping("/{gid}")
    public ModelAndView toGroupTalkPage(@PathVariable Integer gid, @RequestParam(defaultValue="false") Boolean goToTasks, @RequestParam(required=false) String msg){
        Membership membership = membershipService.selectMembershipByUG(WebHelper.getCurrentUser().getId(), gid, false);
        if(membership == null)
            return membershipController.toGroupListPage("You are not the member of this group!");

        ModelAndView mav = new ModelAndView(HtmlPageHelper.GROUP_TALK);
        mav.addObject("group", groupService.selectGroupById(gid, false));
        mav.addObject("myMembership", membership);
        mav.addObject("isAdmin", (membership.getRole().equals(Role.ADMIN) || membership.getRole().equals(Role.FOUNDER)));
        mav.addObject("isFounder", membership.getRole().equals(Role.FOUNDER));
        mav.addObject("goToTasks", goToTasks);
        mav.addObject("msg", msg);
        return mav;
    }

    /**
     * 负责处理群建立者解散群组的请求
     * @param id 目标群组的id
     * @return 转发到群组列表页面
     */
    @DeleteMapping
    public ModelAndView doDeleteGroup(Integer id){
        Membership membership = membershipService.selectMembershipByUG(WebHelper.getCurrentUser().getId(), id, true);

        //如果没有权限，则告诉用户错误信息
        if(membership == null || !membership.getRole().equals(Role.FOUNDER))
            return membershipController.toGroupListPage("You have no right to delete this group!");

        //否则解散群组并告诉用户操作成功
        groupService.deleteGroupById(id);
        return membershipController.toGroupListPage("Deleted successfully!");
    }

    /**
     * 负责处理用户修改群信息的AJAX请求
     * @param group 用户提交的数据
     * @return 修改成功返回"OK"，否则返回错误信息
     */
    @PutMapping
    @ResponseBody
    public String doUpdateGroup(Group group){
        Membership membership = membershipService.selectMembershipByUG(WebHelper.getCurrentUser().getId(), group.getId(), true);

        //如果没有权限，则告诉用户错误信息
        if(membership == null || membership.getRole().equals(Role.NORMAL))
            return "You have no right to update the information this group!";

        groupService.updateGroupByIdSelectively(group);
        return "OK";
    }
}