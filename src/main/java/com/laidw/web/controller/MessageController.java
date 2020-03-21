package com.laidw.web.controller;

import com.laidw.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.text.SimpleDateFormat;
import java.util.Date;

@Controller
@RequestMapping("/message")
public class MessageController extends BaseController {

    @Autowired
    private MessageService messageService;

    @PostMapping
    @ResponseBody
    public String doSendMessage(Boolean isPrivate, Integer senderId, Integer acceptorId, String content){
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
