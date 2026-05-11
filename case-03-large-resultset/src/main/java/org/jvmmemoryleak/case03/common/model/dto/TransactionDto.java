package org.jvmmemoryleak.case03.common.model.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionDto {

    private UUID id;
    private String transactionReference;
    private String type;
    private String status;
    private BigDecimal amount;
    private String currency;
    private String beneficiaryName;
    private String beneficiaryBankName;
    private String beneficiaryCountryCode;
    private String description;
    private String createdBy;
    private LocalDateTime createdAt;
}