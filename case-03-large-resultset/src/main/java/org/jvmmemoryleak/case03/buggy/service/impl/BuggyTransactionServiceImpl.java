package org.jvmmemoryleak.case03.buggy.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jvmmemoryleak.case03.buggy.model.payload.request.BuggyTransactionFilterRequest;
import org.jvmmemoryleak.case03.buggy.service.BuggyTransactionService;
import org.jvmmemoryleak.case03.common.mapper.TransactionMapper;
import org.jvmmemoryleak.case03.common.model.dto.TransactionDto;
import org.jvmmemoryleak.case03.common.repository.TransactionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class BuggyTransactionServiceImpl implements BuggyTransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public List<TransactionDto> findTransactions(BuggyTransactionFilterRequest request) {

        // 🔥 Bug: loads ALL transactions matching the date range into memory at once
        // No pagination — the caller gets everything in one response
        // At 500k records this exhausts the heap
        return transactionRepository
                .findAllByCreatedAtBetween(request.getFrom(), request.getTo())
                .stream()
                .map(transactionMapper::toDto)
                .toList();
    }
}