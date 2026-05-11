package org.jvmmemoryleak.case03.buggy.controller;

import lombok.RequiredArgsConstructor;
import org.jvmmemoryleak.case03.buggy.model.payload.request.BuggyTransactionFilterRequest;
import org.jvmmemoryleak.case03.buggy.service.BuggyTransactionService;
import org.jvmmemoryleak.case03.common.model.dto.TransactionDto;
import org.jvmmemoryleak.case03.common.model.payload.response.ApiResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/case03/buggy/transactions")
@RequiredArgsConstructor
public class BuggyTransactionController {

    private final BuggyTransactionService buggyTransactionService;

    @GetMapping
    public ApiResponse<List<TransactionDto>> findTransactions(@ModelAttribute BuggyTransactionFilterRequest request) {

        return ApiResponse.ok(buggyTransactionService.findTransactions(request));
    }
}