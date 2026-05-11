package org.jvmmemoryleak.case03.fixed.controller;

import lombok.RequiredArgsConstructor;
import org.jvmmemoryleak.case03.common.model.dto.TransactionDto;
import org.jvmmemoryleak.case03.common.model.payload.response.ApiResponse;
import org.jvmmemoryleak.case03.fixed.model.payload.request.FixedTransactionFilterRequest;
import org.jvmmemoryleak.case03.fixed.service.FixedTransactionService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/case03/fixed/transactions")
@RequiredArgsConstructor
public class FixedTransactionController {

    private final FixedTransactionService fixedTransactionService;

    @GetMapping
    public ApiResponse<Page<TransactionDto>> findTransactions(@ModelAttribute FixedTransactionFilterRequest request) {

        return ApiResponse.ok(fixedTransactionService.findTransactions(request));
    }
}