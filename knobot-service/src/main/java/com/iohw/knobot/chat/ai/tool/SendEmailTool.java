package com.iohw.knobot.chat.ai.tool;

import com.iohw.knobot.config.properties.EmailProperties;
import com.iohw.knobot.chat.domain.common.exception.EmailSendException;
import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.P;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * @author: iohw
 * @date: 2025/4/23 22:18
 * @description:
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class SendEmailTool {
    private final JavaMailSenderImpl javaMailSender;
    private final EmailProperties emailProperties;
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Tool("将用户提的建议、问题、Bug信息发送邮件给作者")
    public void sendEmailToAuthor(
            @P("标题") String title,
            @P("用户给出的建议、问题、Bug信息") String issueDescription) {
        log.info("开始发送问题反馈邮件，标题：{}", title);
        title = new StringBuilder("[Knobot]").append(title).toString();
        MimeMessage message = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setSubject("问题反馈：" + title);

            String content = String.format(
                emailProperties.getTemplate(),
                LocalDateTime.now().format(FORMATTER),
                issueDescription
            );
            helper.setText(content, true);
            helper.setTo(emailProperties.getToAddress());
            helper.setFrom(emailProperties.getFromAddress());

            javaMailSender.send(message);
            log.info("问题反馈邮件发送成功");

        } catch (MessagingException e) {
            log.error("发送邮件失败", e);
            throw new EmailSendException("发送邮件失败：" + e.getMessage(), e);
        }
    }
}
