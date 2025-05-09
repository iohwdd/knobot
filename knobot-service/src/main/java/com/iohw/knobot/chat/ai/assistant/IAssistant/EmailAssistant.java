package com.iohw.knobot.chat.ai.assistant.IAssistant;

import com.iohw.knobot.chat.ai.assistant.IAssistant.base.Assistant;
import com.iohw.knobot.chat.domain.vo.request.SubmitIssueCommand;
import dev.langchain4j.service.SystemMessage;

/**
 * @author: iohw
 * @date: 2025/4/23 22:43
 * @description:
 */
public interface EmailAssistant extends Assistant {
    @SystemMessage("将用户提的建议、问题、Bug信息发送邮件给作者")
    String sendEmailToAuthor(SubmitIssueCommand command);

    @SystemMessage("将用户提的建议、问题、Bug信息发送邮件给作者")
    String sendEmailToAuthor(String issue);
}
