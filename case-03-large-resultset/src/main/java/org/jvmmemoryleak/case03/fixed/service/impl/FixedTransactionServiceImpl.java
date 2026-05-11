package org.jvmmemoryleak.case03.fixed.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jvmmemoryleak.case03.common.mapper.TransactionMapper;
import org.jvmmemoryleak.case03.common.model.dto.TransactionDto;
import org.jvmmemoryleak.case03.common.repository.TransactionRepository;
import org.jvmmemoryleak.case03.fixed.model.payload.request.FixedTransactionFilterRequest;
import org.jvmmemoryleak.case03.fixed.service.FixedTransactionService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class FixedTransactionServiceImpl implements FixedTransactionService {

    private final TransactionRepository transactionRepository;
    private final TransactionMapper transactionMapper;

    @Override
    public Page<TransactionDto> findTransactions(FixedTransactionFilterRequest request) {
        // ✅ Fix: caller specifies page and size
        // Only the requested page is loaded into memory
        // Heap usage stays stable regardless of total record count
        return transactionRepository
                .findAllByCreatedAtBetween(request.getFrom(), request.getTo(), PageRequest.of(request.getPage(), request.getSize()))
                .map(transactionMapper::toDto);
    }
}