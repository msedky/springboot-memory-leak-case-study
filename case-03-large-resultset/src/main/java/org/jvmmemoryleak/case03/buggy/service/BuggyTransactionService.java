package org.jvmmemoryleak.case03.buggy.service;

import org.jvmmemoryleak.case03.buggy.model.payload.request.BuggyTransactionFilterRequest;
import org.jvmmemoryleak.case03.common.model.dto.TransactionDto;

import java.time.LocalDateTime;
import java.util.List;

public interface BuggyTransactionService {
    List<TransactionDto> findTransactions(BuggyTransactionFilterRequest request);
}