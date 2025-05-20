package com.iohw.knobot.library.controller;

import com.iohw.knobot.libary.domain.vo.request.QueryDocumentLibRequest;
import com.iohw.knobot.libary.domain.vo.request.QueryLibraryDetailListRequest;
import com.iohw.knobot.libary.domain.vo.request.QueryLibraryListRequest;
import com.iohw.knobot.libary.service.IKnowledgeLibDocumentService;
import com.iohw.knobot.libary.service.IKnowledgeLibService;
import com.iohw.knobot.libary.domain.vo.response.KnowledgeLibDocumentResponse;
import com.iohw.knobot.libary.domain.vo.response.KnowledgeLibNameResponse;
import com.iohw.knobot.libary.domain.vo.response.KnowledgeLibResponse;
import com.iohw.knobot.common.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author: iohw
 * @date: 2025/4/25 22:29
 * @description: 知识库查询控制器
 */
@RestController
@RequestMapping("/library")
@RequiredArgsConstructor
public class LibraryQueryController {
    private final IKnowledgeLibService IKnowledgeLibService;
    private final IKnowledgeLibDocumentService IKnowledgeLibDocumentService;

    @GetMapping("/queryLibraryDetailList")
    public Result<List<KnowledgeLibResponse>> queryLibraryDetailList(QueryLibraryDetailListRequest request) {
        return Result.success(IKnowledgeLibService.queryLibraryDetailList(request));
    }

    @GetMapping("/queryLibraryList")
    public Result<List<KnowledgeLibNameResponse>> queryLibraryList(QueryLibraryListRequest request) {
        return Result.success(IKnowledgeLibService.queryKnowledgeLibList(request));
    }

    @GetMapping("/queryLibraryDocumentList")
    public Result<List<KnowledgeLibDocumentResponse>> queryLibraryDocumentList(QueryDocumentLibRequest request) {
        return Result.success(IKnowledgeLibDocumentService.queryDocumentList(request.getKnowledgeLibId()));
    }
}