package org.jvmmemoryleak.case01.fixed.service;

import lombok.RequiredArgsConstructor;
import org.jvmmemoryleak.case01.common.dto.ProductDto;
import org.jvmmemoryleak.case01.common.entity.ProductEntity;
import org.jvmmemoryleak.case01.common.mapper.ProductMapper;
import org.jvmmemoryleak.case01.common.repository.ProductRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
public class FixedProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CacheManager cacheManager;

    @Cacheable(value = "products", key = "#id")
    public ProductDto getProduct(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseGet(() -> productRepository.save(createProduct(id)));

        return productMapper.toDto(entity);
    }

    private ProductEntity createProduct(Long id) {
        return ProductEntity.builder()
                .id(id)
                .name("Product-" + id)
                .price(BigDecimal.valueOf(99.99))
                .description("X".repeat(10_000))
                .build();
    }

    public Long getCacheSize() {
        Cache cache = cacheManager.getCache("products");

        if (cache == null) {
            return 0L;
        }

        Object nativeCache = cache.getNativeCache();

        if (nativeCache instanceof com.github.benmanes.caffeine.cache.Cache<?, ?> caffeineCache) {
            return caffeineCache.estimatedSize();
        }

        return -1L;
    }

    public void clearCache() {
        Cache cache = cacheManager.getCache("products");

        if (cache != null) {
            cache.clear();
        }
    }
}