package com.laidw.service.impl;

import com.laidw.service.MailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Map;

/**
 * MailService接口的实现类，负责发送邮件
 */
@Service
@Transactional
public class MailServiceImpl implements MailService {

    @Value("${spring.mail.username}")
    private String sendFrom;

    @Autowired
    private JavaMailSender jms;

    @Autowired
    private TemplateEngine engine;

    @Override
    public void sendSimpleMail(String sendTo, String title, String content) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(sendFrom);
        msg.setTo(sendTo);
        msg.setSubject(title);
        msg.setText(content);
        jms.send(msg);
    }

    @Override
    public void sendAttachmentMail(String sendTo, String title, String content, File file) throws Exception {
        MimeMessage msg = jms.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, true);
        helper.setFrom(sendFrom);
        helper.setTo(sendTo);
        helper.setSubject(title);
        helper.setText(content, true);

        FileSystemResource res = new FileSystemResource(file);
        helper.addAttachment("附件", res);
        jms.send(msg);
    }

    @Override
    public void sendTemplateMail(String sendTo, String title, String templateName, Map<String, Object> map) throws Exception{
        MimeMessage msg = jms.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, false);

        helper.setFrom(sendFrom);
        helper.setTo(sendTo);
        helper.setSubject(title);

        Context context = new Context();
        context.setVariables(map);
        String content = engine.process(templateName, context);
        helper.setText(content, true);
        jms.send(msg);
    }
}
