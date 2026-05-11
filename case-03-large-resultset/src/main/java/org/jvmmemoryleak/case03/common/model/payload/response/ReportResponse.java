package org.jvmmemoryleak.case03.common.model.payload.response;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReportResponse {

    private int totalTransactions;
    private long totalCredits;
    private long totalDebits;
    private BigDecimal totalCreditAmount;
    private BigDecimal totalDebitAmount;
    private BigDecimal netAmount;
    private LocalDateTime reportGeneratedAt;
    private String processingStrategy; // "FULL_LOAD" or "PAGINATED"
}