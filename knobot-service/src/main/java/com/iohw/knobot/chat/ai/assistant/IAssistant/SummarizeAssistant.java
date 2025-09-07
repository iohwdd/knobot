package com.iohw.knobot.chat.ai.assistant.IAssistant;

import com.iohw.knobot.chat.ai.assistant.IAssistant.base.Assistant;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * @author: iohw
 * @date: 2025/5/7 10:44
 * @description:
 */
public interface SummarizeAssistant extends Assistant {
    @SystemMessage("总结并概括用户提问，尽可能精简，输出一个标题，字数要求低于20字\n" +
            "例如：用户输入：给我介绍一下netty。 响应：关于netty的介绍 ")
    String summarize(String question);

    @SystemMessage("""
        你是一个高级的对话管理和压缩助手。你的任务是接收一个多轮对话历史，并将其压缩至指定的最大轮数 {{windowLength}}。压缩后的对话必须严格保持用户（USER）与助手（AI）的角色交替，并且要最大限度地保留核心上下文，并且输出内容只为结果JSON字符串，不要附带其它任何内容。
        
        任务规则
        轮次定义：一轮完整的对话被定义为“一条用户消息”和“一条助手消息”的组合。
        
        硬性限制：压缩后的总对话轮数必须精确为 {{windowLength}}。
        
        优先级：如果原始对话超过 {{windowLength}} 轮，优先保留最近的对话内容。
        
        摘要策略：对于所有被压缩或截断的对话轮次，你需要将它们的核心内容提炼为一条简短、精确的摘要。这条摘要应以 AI 助手的身份呈现，并总结被压缩轮次中的关键事实、用户意图或AI的主要回应。
        
        角色交替：压缩后的对话必须严格遵循 USER -> AI -> USER -> AI 的交替顺序，即必须USER开始，AI结尾。你可能需要调整摘要的位置以确保这一点。
        
        无需压缩：如果原始对话轮数少于或等于 {{windowLength}}，则无需任何修改，按原 JSON 格式返回即可，重点只要返回JSON数据。
        
        示例
        以下是当 {{windowLength}} 设定为 3 时，一个多轮对话的压缩流程演示。
        
        输入（原始对话历史，共 5 轮）
        JSON
        
        [
            {
                "contents": [
                    {
                        "text": "你好"
                    }
                ],
                "type": "USER"
            },
            {
                "text": "你好呀！✨ 很高兴见到你！",
                "type": "AI"
            },
            {
                "contents": [
                    {
                        "text": "你是基于什么大模型的"
                    }
                ],
                "type": "USER"
            },
            {
                "text": "我是基于通义千问（Qwen）系列的大模型...",
                "type": "AI"
            },
            {
                "contents": [
                    {
                        "text": "你厉害吗"
                    }
                ],
                "type": "USER"
            },
            {
                "text": "我会尽力而为，帮助你解决问题和提供有用的信息。",
                "type": "AI"
            },
            {
                "contents": [
                    {
                        "text": "知道了"
                    }
                ],
                "type": "USER"
            },
            {
                "text": "嘿嘿，看来你已经接受我的身份了！😎",
                "type": "AI"
            },
            {
                "contents": [
                    {
                        "text": "好的，大王"
                    }
                ],
                "type": "USER"
            }
        ]
        输出（压缩后的对话，共 3 轮）
        JSON
        
        [
            {
                "contents": [
                    {
                        "text": "你好"
                    }
                ],
                "type": "USER"
            },
            {
                "text": "你好呀！✨ 很高兴见到你！",
                "type": "AI"
            },
            {
                "text": "用户询问了我的大模型来源及能力，我回答了基于通义千问，并介绍了我的功能。此后用户表达了认可。",
                "type": "AI"
            },
            {
                "contents": [
                    {
                        "text": "好的，大王"
                    }
                ],
                "type": "USER"
            }
        ]
        """)
    String multiQuerySummarize(@UserMessage String multiQueryRecord, @V("windowLength") int windowLength);
}
