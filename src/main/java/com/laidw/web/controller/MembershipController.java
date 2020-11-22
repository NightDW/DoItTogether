package com.laidw.web.controller;

import com.laidw.entity.Membership;
import com.laidw.service.MembershipService;
import com.laidw.web.tools.HtmlPageHelper;
import com.laidw.web.tools.WebHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;

/**
 * 负责处理群内部的业务
 */
@Controller
@RequestMapping("/membership")
public class MembershipController{

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private GroupController groupController;

    /**
     * 查询当前用户加入的群组，并展示出来
     * @param msg 需要显示的消息，主要是为了方便其它控制器方法调用
     * @return 群组列表页面
     */
    @GetMapping("/my")
    public ModelAndView toGroupListPage(@RequestParam(required = false) String msg) {
        ModelAndView mav = new ModelAndView(HtmlPageHelper.GROUP_LIST);
        List<Membership> memberships = membershipService.selectMembershipsByUserId(WebHelper.getCurrentUser().getId(), false);

        //所有的群聊根据群id、isShow、isTop属性来排序
        Collections.sort(memberships);

        mav.addObject("memberships", memberships);
        mav.addObject("msg", msg);
        return mav;
    }

    /**
     * 用于处理用户发送的置顶/隐藏群组会话的请求
     * @param gid 需要操作的群组的id
     * @param num 代表具体的操作，原生类型是字符串，有4种取值："00", "01", "10", "11"
     * @return 转发到群组列表页面
     */
    @GetMapping("/view/{gid}/{num}")
    public ModelAndView doChangeView(@PathVariable Integer gid, @PathVariable Integer num) {

        //先检查用户是否加入了该群
        Membership membership = membershipService.selectMembershipByUG(WebHelper.getCurrentUser().getId(), gid, true);
        if (membership == null)
            return toGroupListPage("You are not the member of this group!");

        membership = new Membership(gid);

        //第一个字符表示设置isShow或isTop，第二个字符相当于true/false
        if (num < 9) {
            membership.setIsShow(num == 1);
        } else {
            membership.setIsTop(num == 11);
        }
        membershipService.updateMembershipByIdSelectively(membership);
        return toGroupListPage("Changed successfully!");
    }

    /**
     * 处理用户发送的修改群昵称的AJAX请求
     * @param mid      Membership的id
     * @param nickname 修改后的昵称
     * @return 固定返回"OK"
     */
    @GetMapping("/nickname/{mid}")
    @ResponseBody
    public String doChangeNickname(@PathVariable Integer mid, String nickname) {
        Membership membership = new Membership(mid);
        membership.setNickname(nickname);
        membershipService.updateMembershipByIdSelectively(membership);
        return "OK";
    }

    /**
     * 处理群管理员的修改群成员权限的请求
     * @param membership 修改后的数据
     * @param gid        所在群组的id，方便判断用户是否有权限，也可用于重新回到该群组的详细信息页面
     * @return 返回到群组的详细信息页面
     */
    @PutMapping("/right")
    public ModelAndView doUpdateMembershipInfo(Membership membership, Integer gid) {

        //如果当前用户不是群成员，或者权限不高于目标用户，则告诉用户无权修改
        Membership i = membershipService.selectMembershipByUG(WebHelper.getCurrentUser().getId(), gid, true);
        Membership target = membershipService.selectMembershipById(membership.getId(), true);
        if (i == null || i.getRole().compareTo(target.getRole()) >= 0)
            return groupController.toGroupTalkPage(gid, false, "You have no right to do this!");

        membershipService.updateMembershipByIdSelectively(membership);
        return groupController.toGroupTalkPage(gid, false, "Changed successfully");
    }
}