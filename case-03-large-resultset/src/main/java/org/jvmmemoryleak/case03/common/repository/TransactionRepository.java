package org.jvmmemoryleak.case03.common.repository;

import org.jvmmemoryleak.case03.common.model.entity.TransactionEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, UUID> {

    // 🔥 Buggy — loads ALL matching records into memory at once
    List<TransactionEntity> findAllByCreatedAtBetween(LocalDateTime from, LocalDateTime to);

    // ✅ Fixed — returns only the requested page
    Page<TransactionEntity> findAllByCreatedAtBetween(LocalDateTime from, LocalDateTime to, Pageable pageable);
}