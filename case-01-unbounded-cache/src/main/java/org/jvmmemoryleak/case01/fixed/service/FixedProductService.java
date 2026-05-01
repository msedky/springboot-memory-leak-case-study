package org.jvmmemoryleak.case01.fixed.service;

import lombok.RequiredArgsConstructor;
import org.jvmmemoryleak.case01.common.dto.ProductDto;
import org.jvmmemoryleak.case01.common.entity.ProductEntity;
import org.jvmmemoryleak.case01.common.exception.NotFoundException;
import org.jvmmemoryleak.case01.common.mapper.ProductMapper;
import org.jvmmemoryleak.case01.common.payload.request.ProductRequest;
import org.jvmmemoryleak.case01.common.repository.ProductRepository;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FixedProductService {

    private static final String PRODUCTS_CACHE = "products";

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;
    private final CacheManager cacheManager;

    @CachePut(value = PRODUCTS_CACHE, key = "#result.id")
    public ProductDto create(ProductRequest request) {
        ProductEntity productEntity = productRepository.save(productMapper.toEntity(request));
        return productMapper.toDto(productEntity);
    }

    @CachePut(value = PRODUCTS_CACHE, key = "#id")
    public ProductDto update(Long id, ProductRequest request) {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));

        productMapper.toEntity(request, productEntity);

        ProductEntity savedEntity = productRepository.save(productEntity);
        return productMapper.toDto(savedEntity);
    }

    @Cacheable(value = PRODUCTS_CACHE, key = "#id")
    public ProductDto get(Long id) {
        return getFromDb(id);
    }

    @CacheEvict(value = PRODUCTS_CACHE, key = "#id")
    public void delete(Long id) {
        productRepository.deleteById(id);
    }


    public List<ProductDto> getAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toDto)
                .toList();
    }

    public int getCacheSize() {
        Cache cache = cacheManager.getCache(PRODUCTS_CACHE);

        if (cache == null) {
            return 0;
        }

        Object nativeCache = cache.getNativeCache();

        if (nativeCache instanceof com.github.benmanes.caffeine.cache.Cache<?, ?> caffeineCache) {
            return Math.toIntExact(caffeineCache.estimatedSize());
        }

        return -1;
    }

    public void clearCache() {
        Cache cache = cacheManager.getCache(PRODUCTS_CACHE);

        if (cache != null) {
            cache.clear();
        }
    }

    private ProductDto getFromDb(Long id) {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));

        return productMapper.toDto(productEntity);
    }
}