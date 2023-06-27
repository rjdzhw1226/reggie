package com.itheima.reggie.service;

/**
 * MailService
 */
public interface MailService {
    /**
     * 发送邮件
     * @param mail
     */
    void sendMail(String mail,String code);
}
