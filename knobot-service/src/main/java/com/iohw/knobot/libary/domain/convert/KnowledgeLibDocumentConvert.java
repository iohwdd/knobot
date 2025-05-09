package com.iohw.knobot.libary.domain.convert;

import java.util.List;

import org.mapstruct.Mapper;

import com.iohw.knobot.libary.domain.entity.KnowledgeLibDocumentDO;
import com.iohw.knobot.libary.domain.vo.response.KnowledgeLibDocumentResponse;

/**
 * @author: iohw
 * @date: 2025/4/25 23:01
 * @description:
 */
@Mapper(componentModel = "spring")
public interface KnowledgeLibDocumentConvert {
    List<KnowledgeLibDocumentResponse> toVO(List<KnowledgeLibDocumentDO> list);
}
