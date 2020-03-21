package com.laidw.web.controller;

import com.laidw.entity.Friendship;
import com.laidw.service.FriendshipService;
import com.laidw.web.controller.tools.HtmlPageHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

@Controller
@RequestMapping("/friend")
public class FriendController extends BaseController {

    @Autowired
    private FriendshipService friendshipService;

    @Value("${dit.system-user.request-friend-msg}")
    private String requestFriendMsg;

    @GetMapping("/request/{gid}")
    @ResponseBody
    public String doRequestToBeFriends(@PathVariable Integer gid, String remarks, HttpServletRequest request){
        Friendship friendship = friendshipService.selectFriendship(getCurrentUser().getId(), gid);
        if(friendship != null)
            return "NO_NEED";
        String requestUrl = request.getRequestURL().toString();
        String projectUrl = requestUrl.substring(0, requestUrl.lastIndexOf("friend"));
        String userInfo = "<a href='" + projectUrl + "user/" + getCurrentUser().getId() + "'>" + getCurrentUser().getUsername() + "</a>";
        String link = "<a href='" + projectUrl + "friend/agree/" + getCurrentUser().getId() + "'>Click to consent</a>";
        String msg = requestFriendMsg.replace("#user", userInfo).replace("#remarks", remarks).replace("#link", link);
        friendshipService.requestToBecomeFriends(getCurrentUser().getId(), gid, msg);
        return "OK";
    }

    @GetMapping("/my")
    public ModelAndView toMyFriendsPage(){
        ModelAndView mav = new ModelAndView(HtmlPageHelper.USER_LIST);
        List<Friendship> friendships = friendshipService.selectFriendshipsByHostId(getCurrentUser().getId());
        Collections.sort(friendships);
        mav.addObject("friendships", friendships);
        return mav;
    }

    @GetMapping("/{fid}")
    public ModelAndView toFriendTalkPage(@PathVariable Integer fid){
        ModelAndView mav = new ModelAndView(HtmlPageHelper.USER_TALK);
        mav.addObject("friendship", friendshipService.selectFriendshipById(fid));
        return mav;
    }

    @GetMapping("/frame/{fid}")
    public ModelAndView toFriendTalkFrame(@PathVariable Integer fid){
        ModelAndView mav = new ModelAndView(HtmlPageHelper.USER_TALK_FRAME);
        mav.addObject("friendship", friendshipService.selectFriendshipById(fid));
        return mav;
    }

    @GetMapping("/agree/{gid}")
    public ModelAndView agreeToBeFriends(@PathVariable Integer gid){
        ModelAndView mav;
        Integer hid = getCurrentUser().getId();
        boolean hasBeenFriends = friendshipService.selectFriendship(hid, gid) != null;
        if(!hasBeenFriends)
            friendshipService.agreeToBecomeFriends(hid, gid);
        mav = toThisFriendTalkPage(gid);
        mav.addObject("msg", hasBeenFriends ? "You had been friends!" : "Added friend successfully!");
        return mav;
    }

    @GetMapping("/view/{fid}/{num}")
    public ModelAndView doChangeView(@PathVariable Integer fid, @PathVariable Integer num){
        //num是2位二进制数，有4种取值：00 01 10 11
        //第一位表示设置isShow或isTop，第二位相当于true/false
        Friendship friendship = new Friendship();
        friendship.setId(fid);
        if(num < 9)
            friendship.setIsShow(num == 1);
        else
            friendship.setIsTop(num == 11);
        friendshipService.updateFriendshipByIdSelectively(friendship);
        return toMyFriendsPage();
    }

    @GetMapping("/remarks/{fid}")
    @ResponseBody
    public String doChangeRemarks(@PathVariable Integer fid, String remarks){
        Friendship friendship = new Friendship();
        friendship.setId(fid);
        friendship.setRemarks(remarks);
        friendshipService.updateFriendshipByIdSelectively(friendship);
        return "OK";
    }

    public ModelAndView toThisFriendTalkPage(Integer friendUserId){
        ModelAndView mav = new ModelAndView(HtmlPageHelper.USER_TALK);
        mav.addObject("friendship", friendshipService.selectFriendship(getCurrentUser().getId(), friendUserId));
        return mav;
    }
}
