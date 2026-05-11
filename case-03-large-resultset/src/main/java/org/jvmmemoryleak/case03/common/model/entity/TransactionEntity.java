package org.jvmmemoryleak.case03.common.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "transactions")
@EntityListeners(AuditingEntityListener.class)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TransactionEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String transactionReference;

    @Column(nullable = false, length = 10)
    private String type; // CREDIT, DEBIT, TRANSFER, FEE

    @Column(nullable = false, length = 10)
    private String status; // PENDING, COMPLETED, FAILED, REVERSED

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal amount;

    @Column(length = 3)
    private String currency; // ISO 4217

    @Embedded
    private BeneficiaryDetails beneficiary;

    // Populated for ~30% of transactions (international SWIFT transfers only)
    @Embedded
    private IntermediaryBankDetails intermediaryBank;

    // Populated for ~30% of transactions (cross-currency transfers only)
    @Embedded
    private ExchangeRateDetails exchangeRate;

    @Column(length = 255)
    private String description;

    @Column(nullable = false)
    private String createdBy;

    @Version
    private Integer version;

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(nullable = false)
    private LocalDateTime updatedAt;
}