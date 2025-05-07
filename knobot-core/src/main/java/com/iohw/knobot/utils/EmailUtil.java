package com.iohw.knobot.utils;


import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

/**
 * @author: iohw
 * @date: 2025/5/7 23:20
 * @description:
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class EmailUtil {
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;
    public void sendCodeVerifyEmail(String to, String code) {
        String subject = "绑定邮箱验证码校验";
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setSubject("【Knobot】 " + subject);
            helper.setTo(to);
            helper.setFrom(from);
            helper.setText(code, true);
            mailSender.send(message);
            log.info("邮箱验证码发送成功： {}", code);
        } catch (MessagingException e) {
            log.info("邮箱验证码发送失败");
            throw new RuntimeException("邮箱发送失败");
        }
    }
}
