package com.laidw.web.controller;

import com.laidw.service.MembershipService;
import com.laidw.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/message")
public class MessageController extends BaseController {

    @Autowired
    private MessageService messageService;

    @Autowired
    private MembershipService membershipService;

    @PostMapping
    @ResponseBody
    public String doSendMessage(Boolean isPrivate, Integer senderId, Integer acceptorId, String content){
        //如果是群成员发送消息，则先判断他是否被禁言了
        if(!isPrivate && membershipService.selectMembership(getCurrentUser().getId(), acceptorId, true).getIsMute())
            return "Failed";
        String result = messageService.sendMsg(isPrivate, senderId, acceptorId, content) + ";";
        result += new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
        return result;
    }

    @DeleteMapping
    @ResponseBody
    public String doDeleteMessage(Integer mid){
        messageService.deleteMessageById(mid);
        return "OK";
    }
}
