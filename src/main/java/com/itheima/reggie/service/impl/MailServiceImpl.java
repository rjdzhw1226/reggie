package com.itheima.reggie.service.impl;

import com.itheima.reggie.service.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

/**
 * @Author QG
 * @Date 2023/1/24 13:51
 **/
@Service
@Slf4j
public class MailServiceImpl implements MailService {

    @Autowired
    private JavaMailSender javaMailSender;

    @Value("${spring.mail.username}")
    private String from;

    private static final String text = "验证码有效期为五分钟，登录验证码为：";

    /**
     * 发送邮件
     * @param to 目的邮箱
     */
    @Override
    public void sendMail(String to,String code) {
        SimpleMailMessage message = new SimpleMailMessage();
        System.out.println(from);
        message.setFrom(from + "(十岁卖切糕、)");
        message.setTo(to);
        message.setSubject("登录验证码");
        message.setText(text+code);
        try {
            javaMailSender.send(message);
        } catch (MailException e) {
            log.debug("邮件发送失败，请稍后重试");
            throw e;
        }
    }
}
