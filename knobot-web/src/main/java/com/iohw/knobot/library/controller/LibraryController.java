package com.iohw.knobot.library.controller;

import com.iohw.knobot.libary.domain.vo.request.CreateKnowledgeLibCommand;
import com.iohw.knobot.libary.domain.vo.request.CreateKnowledgeLibDocCommand;
import com.iohw.knobot.libary.domain.vo.request.DeleteKnowledgeLibCommand;
import com.iohw.knobot.libary.domain.vo.request.DeleteKnowledgeLibDocCommand;
import com.iohw.knobot.libary.domain.vo.request.UpdateKnowledgeLibCommand;
import com.iohw.knobot.libary.domain.vo.request.UpdateKnowledgeLibDocCommand;
import com.iohw.knobot.libary.service.IKnowledgeLibDocumentService;
import com.iohw.knobot.libary.service.IKnowledgeLibService;
import com.iohw.knobot.common.Result;
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
    private final IKnowledgeLibService IKnowledgeLibService;
    private final IKnowledgeLibDocumentService IKnowledgeLibDocumentService;

    @PostMapping("/createKnowledgeLib")
    public Result<Void> createKnowledgeLib(@RequestBody CreateKnowledgeLibCommand command) {
        IKnowledgeLibService.createKnowledgeLib(command);
        return Result.success(null);
    }

    @PostMapping("/updateKnowledgeLib")
    public Result<Void> updateKnowledgeLib(@RequestBody UpdateKnowledgeLibCommand command) {
        IKnowledgeLibService.updateKnowledgeLib(command);
        return Result.success(null);
    }

    @PostMapping("/createKnowledgeLibDocument")
    public Result<Void> createKnowledgeLibDocument(CreateKnowledgeLibDocCommand command) {
        IKnowledgeLibDocumentService.addDocument(command);
        return Result.success(null);
    }

    @PostMapping("/updateKnowledgeLibDocument")
    public Result<Void> updateKnowledgeLibDocument(@RequestBody UpdateKnowledgeLibDocCommand command) {
        IKnowledgeLibDocumentService.updateDocument(command);
        return Result.success(null);
    }

    @PostMapping("/deleteKnowledgeLibDocument")
    public Result<Void> deleteKnowledgeLibDocument(@RequestBody DeleteKnowledgeLibDocCommand command) {
        IKnowledgeLibDocumentService.deleteDocument(command);
        return Result.success(null);
    }

    @PostMapping("/deleteKnowledgeLib")
    public Result<Void> deleteKnowledgeLib(@RequestBody DeleteKnowledgeLibCommand command) {
        IKnowledgeLibService.deleteKnowledgeLib(command);
        return Result.success(null);
    }
}