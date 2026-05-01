package org.jvmmemoryleak.case01.common.repository;

import org.jvmmemoryleak.case01.common.entity.ProductEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
}