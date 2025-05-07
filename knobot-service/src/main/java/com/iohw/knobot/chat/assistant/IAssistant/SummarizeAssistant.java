package com.iohw.knobot.chat.assistant.IAssistant;

import com.iohw.knobot.chat.assistant.IAssistant.base.Assistant;
import dev.langchain4j.service.SystemMessage;

/**
 * @author: iohw
 * @date: 2025/5/7 10:44
 * @description:
 */
public interface SummarizeAssistant extends Assistant {
    @SystemMessage("总结并概括用户提问，尽可能精简，输出一个标题，字数要求低于20字\n" +
            "例如：用户输入：给我介绍一下netty。 响应：关于netty的介绍 ")
    String summarize(String question);
}
