package com.laidw.service;

import java.io.File;
import java.util.Map;

/**
 * 该Service负责发送邮件
 */
public interface MailService {
    /**
     * 发送简单的邮件
     * @param sendTo 收件人
     * @param title 邮件标题
     * @param content 邮件内容
     */
    void sendSimpleMail(String sendTo, String title, String content);

    /**
     * 发送带附件的邮件
     * @param sendTo 收件人
     * @param title 邮件标题
     * @param content 邮件内容
     * @param file 邮件附件
     */
    void sendAttachmentMail(String sendTo, String title, String content, File file) throws Exception;

    /**
     * 发送模板邮件
     * @param sendTo 收件人
     * @param title 邮件标题
     * @param templateName 模板名称
     * @param map 给该模板传递的数据，供模板取出
     */
    void sendTemplateMail(String sendTo, String title, String templateName, Map<String, Object> map) throws Exception;
}