package com.laidw.web.controller;

import com.laidw.entity.Membership;
import com.laidw.entity.Message;
import com.laidw.entity.User;
import com.laidw.service.MembershipService;
import com.laidw.service.MessageService;
import com.laidw.web.tools.WebHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * 负责处理消息相关的业务
 */
@Controller
@RequestMapping("/message")
public class MessageController{

    @Autowired
    private MessageService messageService;

    @Autowired
    private MembershipService membershipService;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * 处理用户发送的发送消息的AJAX请求
     * @param isPrivate 是否为私聊信息
     * @param senderId 发送者的id，是UserId或者MembershipId
     * @param acceptorId 接收者的id，是UserId或者GroupId
     * @param content 消息的内容
     * @return 成功的话，返回消息的id和发送时间，否则返回错误信息
     */
    @PostMapping
    @ResponseBody
    public String doSendMessage(Boolean isPrivate, Integer senderId, Integer acceptorId, String content){

        //如果是群成员发送消息，则先判断他是否被禁言了
        if(!isPrivate && membershipService.selectMembershipByUG(WebHelper.getCurrentUser().getId(), acceptorId, true).getIsMute())
            return "Failed to send because you are muted!";

        return messageService.sendMsgReturnId(isPrivate, senderId, acceptorId, content) + ";" + sdf.format(new Date());
    }

    /**
     * 处理用户发送的删除消息的AJAX请求
     * @param mid 消息的id
     * @return 处理结果
     */
    @DeleteMapping
    @ResponseBody
    public String doDeleteMessage(Integer mid){
        Message message = messageService.selectMessageById(mid);
        if(message == null)
            return "Message not existed!";

        Integer currentUserId = WebHelper.getCurrentUser().getId();

        //如果是私聊，则只能删除自己发送的消息
        if(message.getIsPrivate()){
            User senderUser = (User)message.getSenderPubInfo();
            if(!senderUser.getId().equals(currentUserId))
                return "Cannot delete because the message sender is not you!";

        //如果是群留言，则能删除自己发送的消息；当自己是管理员时，还可以删除发送者权限低于自己的消息
        } else{
            Membership senderMem = (Membership)message.getSenderPubInfo();
            if(!senderMem.getUserPubInfo().getId().equals(currentUserId)){
                Membership iMem = membershipService.selectMembershipByUG(currentUserId, senderMem.getGroupPubInfo().getId(), true);
                if(iMem.getRole().compareTo(senderMem.getRole()) >= 0)
                    return "Cannot delete because you have no enough right!";
            }
        }
        messageService.deleteMessageById(mid);
        return "OK";
    }
}