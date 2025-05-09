package com.iohw.knobot.utils;


import com.iohw.knobot.common.exception.EmailException;
import com.iohw.knobot.config.properties.EmailProperties;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author: iohw
 * @date: 2025/5/7 23:20
 * @description:
 */
@Slf4j
@Configuration
@RequiredArgsConstructor
public class EmailUtil {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private final JavaMailSender mailSender;
    private final EmailProperties emailProperties;

    @Value("${spring.mail.username}")
    private String from;

    /**
     * 发送邮箱验证码
     * @param to 用户邮箱
     * @param code 验证码
     */
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
            throw new EmailException("邮箱验证码发送失败");
        }
    }

    /**
     * 发送用户反馈信息邮件给作者
     * @param subject
     * @param content
     */
    public void sendFeedbackEmail(String subject, String content) {
        MimeMessage message = mailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setSubject(subject);
            helper.setTo(emailProperties.getToAddress());
            helper.setFrom(emailProperties.getFromAddress());
            content = String.format(
                    emailProperties.getTemplate(),
                    LocalDateTime.now().format(FORMATTER),
                    content
            );
            helper.setText(content, true);
            mailSender.send(message);
            log.info("用户反馈已发送到作者邮箱");
        } catch (MessagingException e) {
            log.error("用户反馈邮件发送失败");
            throw new EmailException("用户反馈邮件发送失败");
        }
    }
}
