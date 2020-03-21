package com.laidw.web.controller;

import com.laidw.entity.Membership;
import com.laidw.service.MembershipService;
import com.laidw.web.controller.tools.HtmlPageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/membership")
public class MembershipController extends BaseController {

    @Autowired
    private MembershipService membershipService;

    @Autowired
    private GroupController groupController;

    @GetMapping("/my")
    public ModelAndView toGroupListPage(){
        ModelAndView mav = new ModelAndView(HtmlPageHelper.GROUP_LIST);
        List<Membership> memberships = membershipService.selectMembershipsByUserId(getCurrentUser().getId(), false);

        //所有的群聊根据群id、isShow、isTop属性来排序
        Collections.sort(memberships);
        mav.addObject("memberships", memberships);
        return mav;
    }

    @GetMapping("/view/{gid}/{num}")
    public ModelAndView doChangeView(@PathVariable Integer gid, @PathVariable Integer num){
        //num是2位二进制数，有4种取值：00 01 10 11
        //第一位表示设置isShow或isTop，第二位相当于true/false
        Membership membership = new Membership(gid);
        if(num < 9)
            membership.setIsShow(num == 1);
        else
            membership.setIsTop(num == 11);
        membershipService.updateMembershipByIdSelectively(membership);
        return toGroupListPage();
    }

    @GetMapping("/nickname/{mid}")
    @ResponseBody
    public String doChangeNickname(@PathVariable Integer mid, String nickname){
        Membership membership = new Membership(mid);
        membership.setNickname(nickname);
        membershipService.updateMembershipByIdSelectively(membership);
        return "OK";
    }

    @PutMapping("/right")
    public ModelAndView doUpdateMembershipInfo(Membership membership, Integer gid){
        membershipService.updateMembershipByIdSelectively(membership);
        ModelAndView mav = groupController.toGroupTalkPage(gid, false);
        mav.addObject("msg", "Changed successfully!");
        return mav;
    }
}
