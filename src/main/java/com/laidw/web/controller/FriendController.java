package com.laidw.web.controller;

import com.laidw.entity.Friendship;
import com.laidw.entity.User;
import com.laidw.service.FriendshipService;
import com.laidw.service.MessageService;
import com.laidw.service.UserService;
import com.laidw.web.tools.HtmlPageHelper;
import com.laidw.web.tools.WebHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.List;

/**
 * 主要处理好友相关的业务
 */
@Controller
@RequestMapping("/friend")
public class FriendController{

    @Autowired
    private FriendshipService friendshipService;

    @Autowired
    private MessageService messageService;

    @Autowired
    private UserService userService;

    @Value("${dit.msg.request-friend}")
    private String requestFriendMsg;

    @Value("${dit.msg.become-friend}")
    private String becomeFriendMsg;

    private String notMyFriendshipMsg = "You are not the host user of this friendship!";


    /**
     * 处理请求添加为好友的AJAX请求
     * @param gid 当前用户想添加的好友的id
     * @param remarks 当前用户给对方提供的验证信息
     * @param request HTTP请求对象
     * @return 返回"NO_NEED"代表已经是好友了，返回"OK"代表成功向对方发送了好友请求
     */
    @GetMapping("/request/{gid}")
    @ResponseBody
    public String doRequestToBeFriends(@PathVariable Integer gid, String remarks, HttpServletRequest request){
        User currentUser = WebHelper.getCurrentUser();

        //如果对方已经是当前用户的好友了，则返回"NO_NEED"
        Friendship friendship = friendshipService.selectFriendshipByHG(currentUser.getId(), gid);
        if(friendship != null)
            return "NO_NEED";

        //否则，拼接一个同意的链接，然后向目标用户发送系统消息
        //注意，验证链接需要拼接上申请者的验证码，用于证明申请者确实是发送了申请
        String projectUrl = WebHelper.getProjectUrl(request, "friend");
        String userInfoLink = "<a href='" + projectUrl + "user/" + currentUser.getId() + "'>" + currentUser.getUsername() + "</a>";
        String agreeLink = "<a href='" + projectUrl + "friend/agree/" + currentUser.getId() + "?verifyCode=" + currentUser.getVerifyCode() + "'>Click to consent</a>";
        String msg = requestFriendMsg.replace("#user", userInfoLink).replace("#remarks", remarks).replace("#link", agreeLink);
        messageService.sendSystemMsgTo(gid, msg);
        return "OK";
    }

    /**
     * 处理用户的同意好友申请的请求
     * @param gid doRequestToBeFriends()方法中的主用户（即发起好友申请的用户）的id
     * @return 转发到当前用户与目标用户的私聊页面
     */
    @GetMapping("/agree/{gid}")
    public ModelAndView agreeToBeFriends(@PathVariable Integer gid, String verifyCode){
        Integer hid = WebHelper.getCurrentUser().getId();

        //如果目标用户是自己
        if(hid.equals(gid))
            return toMyFriendsPage("Error! You cannot be friends with yourself!");

        //如果目标用户的验证码和当前用户提供的验证码不一致，说明目标用户可能根本没有发送好友申请
        User guest = userService.selectUserByCondition(new User(gid), true);
        if(!guest.getVerifyCode().equals(verifyCode))
            return toMyFriendsPage("VerifyCode Error!");

        boolean hasBeenFriends = friendshipService.selectFriendshipByHG(hid, gid) != null;

        //如果两人还不是好友，则添加两条好友记录，然后当前用户向目标用户发送一条消息
        if(!hasBeenFriends){
            friendshipService.insertFriendshipPair(hid, gid);
            messageService.sendMsgReturnId(true, hid, gid, becomeFriendMsg);
        }

        //转发到与目标用户的单独的一个私聊页面，并添加提示信息
        //ModelAndView mav = new ModelAndView(HtmlPageHelper.USER_TALK);
        //mav.addObject("friendship", friendshipService.selectFriendshipByHG(WebHelper.getCurrentUser().getId(), gid));
        //mav.addObject("msg", hasBeenFriends ? "You had been friends!" : "Became friends successfully!");

        //这里我选择直接转发到好友列表页面，同时立刻打开与该好友的聊天页面
        ModelAndView mav = toMyFriendsPage(hasBeenFriends ? "You had been friends!" : "Became friends successfully!");
        mav.addObject("activeUserId", gid);
        return mav;
    }

    /**
     * 查询出当前用户的所有好友并显示
     * @param msg 需要给目标页面添加的消息，这个参数主要是提供给本Controller的其它方法的
     * @return 转发到好友列表页面
     */
    @GetMapping("/my")
    public ModelAndView toMyFriendsPage(@RequestParam(required=false) String msg){
        ModelAndView mav = new ModelAndView(HtmlPageHelper.USER_LIST);

        //查询出所有好友，并根据isTop等属性对好友进行排序
        List<Friendship> friendships = friendshipService.selectFriendshipsByHostId(WebHelper.getCurrentUser().getId());
        Collections.sort(friendships);

        //给ModelAndView添加Object
        mav.addObject("friendships", friendships);
        mav.addObject("msg", msg);
        return mav;
    }

    /**
     * 前往某个Friendship的私聊页面，该Friendship的主用户必须是当前用户
     * @param fid Friendship的id
     * @return 失败则转发到当前用户的好友列表页面，成功则转发到主用户和客用户之间的私聊页面
     */
    @GetMapping("/{fid}")
    public ModelAndView toFriendTalkPage(@PathVariable Integer fid){
        Friendship friendship = getMyFriendship(fid);

        //如果主用户不是当前用户，则转发到当前用户的好友列表页面，并提示错误信息
        if(friendship == null)
            return toMyFriendsPage(notMyFriendshipMsg);

        //否则前往与目标用户的私聊页面
        ModelAndView mav = new ModelAndView(HtmlPageHelper.USER_TALK);
        mav.addObject("friendship", friendship);
        return mav;
    }

    /**
     * 前往某个Friendship的私聊页面，该Friendship的主用户必须是当前用户
     * 注意该页面是其它页面通过iframe标签来引用的
     * @param fid Friendship的id
     * @return 失败则转发到当前用户的好友列表页面，成功则转发到主用户和客用户之间的私聊页面
     */
    @GetMapping("/frame/{fid}")
    public ModelAndView toFriendTalkFrame(@PathVariable Integer fid){
        Friendship friendship = getMyFriendship(fid);

        //如果主用户不是当前用户，则转发到当前用户的好友列表页面，并提示错误信息
        if(friendship == null)
            return toMyFriendsPage(notMyFriendshipMsg);

        //否则前往与目标用户的私聊的frame页面
        ModelAndView mav = new ModelAndView(HtmlPageHelper.USER_TALK_FRAME);
        mav.addObject("friendship", friendship);
        return mav;
    }

    /**
     * 用于处理用户发送的置顶/隐藏好友会话的请求
     * @param fid 需要操作的Friendship的id
     * @param num 代表具体的操作，原生类型是字符串，有4种取值："00", "01", "10", "11"
     * @return 转发到好友列表页面
     */
    @GetMapping("/view/{fid}/{num}")
    public ModelAndView doChangeView(@PathVariable Integer fid, @PathVariable Integer num){
        Friendship friendship = getMyFriendship(fid);

        //如果主用户不是当前用户，则转发到当前用户的好友列表页面，并提示错误信息
        if(friendship == null)
            return toMyFriendsPage(notMyFriendshipMsg);

        friendship = new Friendship();
        friendship.setId(fid);

        //第一个字符表示设置isShow或isTop，第二个字符相当于true/false
        if(num < 9){
            friendship.setIsShow(num == 1);
        } else{
            friendship.setIsTop(num == 11);
        }

        friendshipService.updateFriendshipByIdSelectively(friendship);
        return toMyFriendsPage("Changed successfully!");
    }

    /**
     * 处理用户的修改好友备注的AJAX请求
     * @param fid 要修改的Friendship的id
     * @param remarks 要设置的备注
     * @return 成功则返回"OK"，失败则返回错误信息
     */
    @GetMapping("/remarks/{fid}")
    @ResponseBody
    public String doChangeRemarks(@PathVariable Integer fid, String remarks){
        Friendship friendship = getMyFriendship(fid);
        if(friendship == null)
            return notMyFriendshipMsg;

        friendship = new Friendship();
        friendship.setId(fid);
        friendship.setRemarks(remarks);
        friendshipService.updateFriendshipByIdSelectively(friendship);
        return "OK";
    }

    /**
     * 检查指定的Friendship中的主用户是否为当前用户
     * @param fid Friendship的id
     * @return 当前用户是主用户则返回该Friendship，否则返回null
     */
    private Friendship getMyFriendship(Integer fid){
        Friendship friendship = friendshipService.selectFriendshipById(fid);
        return WebHelper.getCurrentUser().getId().equals(friendship.getHostId()) ? friendship : null;
    }
}