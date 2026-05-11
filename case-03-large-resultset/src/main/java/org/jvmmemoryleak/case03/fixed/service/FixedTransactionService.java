package org.jvmmemoryleak.case03.fixed.service;

import org.jvmmemoryleak.case03.common.model.dto.TransactionDto;
import org.jvmmemoryleak.case03.fixed.model.payload.request.FixedTransactionFilterRequest;
import org.springframework.data.domain.Page;

public interface FixedTransactionService {
    Page<TransactionDto> findTransactions(FixedTransactionFilterRequest request);
}