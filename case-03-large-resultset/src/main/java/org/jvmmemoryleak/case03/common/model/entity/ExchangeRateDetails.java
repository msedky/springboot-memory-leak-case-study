package org.jvmmemoryleak.case03.common.model.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExchangeRateDetails {

    @Column(name = "exchange_rate", precision = 10, scale = 6)
    private BigDecimal rate;

    @Column(name = "target_currency", length = 3)
    private String targetCurrency;

    @Column(name = "target_amount", precision = 19, scale = 4)
    private BigDecimal targetAmount;

    @Column(name = "rate_locked_at")
    private LocalDateTime rateLockedAt;
}