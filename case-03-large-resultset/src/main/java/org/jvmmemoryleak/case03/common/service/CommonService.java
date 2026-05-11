package org.jvmmemoryleak.case03.common.service;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jvmmemoryleak.case03.common.model.entity.BeneficiaryDetails;
import org.jvmmemoryleak.case03.common.model.entity.ExchangeRateDetails;
import org.jvmmemoryleak.case03.common.model.entity.IntermediaryBankDetails;
import org.jvmmemoryleak.case03.common.model.entity.TransactionEntity;
import org.jvmmemoryleak.case03.common.repository.TransactionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

@Service
@RequiredArgsConstructor
@Slf4j
public class CommonService {

    private final TransactionRepository transactionRepository;

    @PersistenceContext
    private EntityManager entityManager;

    private static final int BATCH_SIZE = 1000;

    private static final String[] TYPES = {"CREDIT", "DEBIT", "TRANSFER", "FEE"};
    private static final String[] STATUSES = {"PENDING", "COMPLETED", "FAILED", "REVERSED"};
    private static final String[] CURRENCIES = {"USD", "EUR", "GBP", "EGP", "SAR", "AED"};
    private static final String[] COUNTRIES = {"US", "GB", "DE", "EG", "SA", "AE", "FR", "JP"};
    private static final String[] BANKS = {"JPMorgan Chase", "HSBC", "Deutsche Bank",
            "BNP Paribas", "CIB Egypt", "Riyad Bank"};
    private static final String[] USERS = {"SYSTEM", "user-001", "user-002",
            "user-003", "user-004", "user-005"};
    private static final String[] DESCRIPTIONS = {
            "Monthly salary transfer",
            "Invoice payment - vendor services",
            "International wire transfer",
            "Loan repayment installment",
            "Dividend distribution",
            "Refund for cancelled order",
            "Tax payment to authority",
            "FX conversion fee",
            "Interbank settlement",
            "Customer deposit"
    };

    private static final Random RANDOM = new Random();

    @Transactional
    public Map<String, Object> seed(int count) {
        int inserted = 0;
        List<TransactionEntity> batch = new ArrayList<>(BATCH_SIZE);

        for (int i = 0; i < count; i++) {

            boolean isInternational = RANDOM.nextDouble() < 0.30;
            LocalDateTime txDate = randomDateThisMonth();

            TransactionEntity tx = TransactionEntity.builder()
                    .transactionReference(generateReference(i))
                    .type(TYPES[RANDOM.nextInt(TYPES.length)])
                    .status(STATUSES[RANDOM.nextInt(STATUSES.length)])
                    .amount(randomAmount())
                    .currency(CURRENCIES[RANDOM.nextInt(CURRENCIES.length)])
                    .beneficiary(randomBeneficiary())
                    .intermediaryBank(isInternational ? randomIntermediaryBank() : null)
                    .exchangeRate(isInternational ? randomExchangeRate() : null)
                    .description(DESCRIPTIONS[RANDOM.nextInt(DESCRIPTIONS.length)])
                    .createdBy(USERS[RANDOM.nextInt(USERS.length)])
                    .createdAt(txDate)
                    .updatedAt(txDate)
                    .build();

            batch.add(tx);

            if (batch.size() == BATCH_SIZE) {
                transactionRepository.saveAll(batch);

                // ✅ Flush pending SQL to DB then clear Hibernate session cache
                // Without this, every saved entity stays referenced in the session
                // causing the heap to grow continuously during seeding
                entityManager.flush();
                entityManager.clear();

                batch.clear();
                inserted += BATCH_SIZE;
                log.info("Inserted {} / {}", inserted, count);
            }
        }

        if (!batch.isEmpty()) {
            transactionRepository.saveAll(batch);
            entityManager.flush();
            entityManager.clear();
            inserted += batch.size();
        }

        return Map.of(
                "inserted", inserted,
                "message", "Seeding completed successfully"
        );
    }

    private String generateReference(int index) {
        return String.format("TXN-2026-%s-%06d",
                UUID.randomUUID().toString().substring(0, 6).toUpperCase(), index);
    }

    private BigDecimal randomAmount() {
        double amount = 10 + (RANDOM.nextDouble() * 99990);
        return BigDecimal.valueOf(amount).setScale(4, RoundingMode.HALF_UP);
    }

    private BeneficiaryDetails randomBeneficiary() {
        String country = COUNTRIES[RANDOM.nextInt(COUNTRIES.length)];
        return BeneficiaryDetails.builder()
                .beneficiaryName("Beneficiary-" + RANDOM.nextInt(100000))
                .accountNumber("IBAN" + String.format("%020d", Math.abs(RANDOM.nextLong())))
                .bankName(BANKS[RANDOM.nextInt(BANKS.length)])
                .swiftBic("SWIFT" + String.format("%05d", RANDOM.nextInt(99999)))
                .routingNumber(String.format("%09d", RANDOM.nextInt(999999999)))
                .countryCode(country)
                .physicalAddress(RANDOM.nextInt(999) + " Main St, " + country)
                .build();
    }

    private IntermediaryBankDetails randomIntermediaryBank() {
        return IntermediaryBankDetails.builder()
                .intermediaryBankName(BANKS[RANDOM.nextInt(BANKS.length)])
                .intermediarySwiftBic("ISWIFT" + String.format("%05d", RANDOM.nextInt(99999)))
                .intermediaryRoutingNumber(String.format("%09d", RANDOM.nextInt(999999999)))
                .build();
    }

    private ExchangeRateDetails randomExchangeRate() {
        BigDecimal rate = BigDecimal.valueOf(0.5 + RANDOM.nextDouble() * 2)
                .setScale(6, RoundingMode.HALF_UP);
        BigDecimal amount = randomAmount();
        return ExchangeRateDetails.builder()
                .rate(rate)
                .targetCurrency(CURRENCIES[RANDOM.nextInt(CURRENCIES.length)])
                .targetAmount(amount.multiply(rate).setScale(4, RoundingMode.HALF_UP))
                .rateLockedAt(LocalDateTime.now().minusMinutes(RANDOM.nextInt(60)))
                .build();
    }

    private LocalDateTime randomDateThisMonth() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime start = now.withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        long secondsBetween = java.time.Duration.between(start, now).getSeconds();
        long randomSeconds = (long) (RANDOM.nextDouble() * secondsBetween);
        return start.plusSeconds(randomSeconds);
    }
}
