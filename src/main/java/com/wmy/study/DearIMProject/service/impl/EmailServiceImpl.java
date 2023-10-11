package com.wmy.study.DearIMProject.service.impl;

import com.wmy.study.DearIMProject.domain.SecurityCode;
import com.wmy.study.DearIMProject.service.IEmailService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class EmailServiceImpl implements IEmailService {

    @Value("${mail.fromMail.sender}")
    private String sender; // 发送方

    @Value("${application.name}")
    private String appName;

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public boolean sendSecurityCodeToEmail(SecurityCode code) {
        if (code == null) {
            return false;
        }

        String toEmail = code.getUniKey();
        String content = "<html><body><h3>" + code.getCode() + "</h3></body></html>";
        MimeMessage message = mailSender.createMimeMessage();

        // true表示需要创建一个multipart message
        MimeMessageHelper helper = null;
        try {
            helper = new MimeMessageHelper(message, true);
            helper.setFrom(sender);
            helper.setTo(toEmail + "(" + appName + "验证码通知)");
            helper.setSubject(appName + "验证码");
            helper.setText(content, true);

            mailSender.send(message);
            log.info("Html邮件发送成功！");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }
        return true;
    }
}
