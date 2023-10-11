package com.wmy.study.DearIMProject.service;

import com.wmy.study.DearIMProject.domain.SecurityCode;
import jakarta.mail.MessagingException;

public interface IEmailService {
    /**
     * 发送验证码电子邮件
     *
     * @param code
     * @return
     */
    boolean sendSecurityCodeToEmail(SecurityCode code) throws MessagingException;
}
