package com.iohw.knobot.library.controller;

import com.iohw.knobot.libary.service.KnowledgeLibDocumentService;
import com.iohw.knobot.libary.service.KnowledgeLibService;
import com.iohw.knobot.library.request.*;
import com.iohw.knobot.common.response.Result;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

/**
 * @author: iohw
 * @date: 2025/4/24 21:25
 * @description: 知识库管理控制器
 */
@RestController
@RequestMapping("/library")
@RequiredArgsConstructor
public class LibraryController {
    private final KnowledgeLibService knowledgeLibService;
    private final KnowledgeLibDocumentService knowledgeLibDocumentService;

    @PostMapping("/createKnowledgeLib")
    public Result<Void> createKnowledgeLib(@RequestBody CreateKnowledgeLibCommand command) {
        knowledgeLibService.createKnowledgeLib(command);
        return Result.success(null);
    }

    @PostMapping("/updateKnowledgeLib")
    public Result<Void> updateKnowledgeLib(@RequestBody UpdateKnowledgeLibCommand command) {
        knowledgeLibService.updateKnowledgeLib(command);
        return Result.success(null);
    }

    @PostMapping("/createKnowledgeLibDocument")
    public Result<Void> createKnowledgeLibDocument(CreateKnowledgeLibDocCommand command) {
        knowledgeLibDocumentService.addDocument(command);
        return Result.success(null);
    }

    @PostMapping("/updateKnowledgeLibDocument")
    public Result<Void> updateKnowledgeLibDocument(@RequestBody UpdateKnowledgeLibDocCommand command) {
        knowledgeLibDocumentService.updateDocument(command);
        return Result.success(null);
    }

    @PostMapping("/deleteKnowledgeLibDocument")
    public Result<Void> deleteKnowledgeLibDocument(@RequestBody DeleteKnowledgeLibDocCommand command) {
        knowledgeLibDocumentService.deleteDocument(command);
        return Result.success(null);
    }

    @PostMapping("/deleteKnowledgeLib")
    public Result<Void> deleteKnowledgeLib(@RequestBody DeleteKnowledgeLibCommand command) {
        knowledgeLibService.deleteKnowledgeLib(command);
        return Result.success(null);
    }
}