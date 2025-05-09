package com.iohw.knobot.chat.domain.vo.request;

import dev.langchain4j.model.output.structured.Description;
import lombok.Builder;
import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/4/23 23:12
 * @description:
 */
@Data
@Builder
@Description("用户提出的建议、问题、bug信息")
public class SubmitIssueCommand {
    @Description("标题")
    private String title;
    @Description("建议、问题、bug信息的详细描述")
    private String issueDescription;
}
