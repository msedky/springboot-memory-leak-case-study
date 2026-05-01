package org.jvmmemoryleak.case01.buggy.service;

import lombok.RequiredArgsConstructor;
import org.jvmmemoryleak.case01.common.dto.ProductDto;
import org.jvmmemoryleak.case01.common.entity.ProductEntity;
import org.jvmmemoryleak.case01.common.exception.NotFoundException;
import org.jvmmemoryleak.case01.common.mapper.ProductMapper;
import org.jvmmemoryleak.case01.common.payload.request.ProductRequest;
import org.jvmmemoryleak.case01.common.repository.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class BuggyProductService {

    private static final Map<Long, ProductDto> CACHE = new ConcurrentHashMap<>();

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductDto create(ProductRequest request) {
        ProductEntity productEntity = productRepository.save(productMapper.toEntity(request));
        ProductDto productDto = productMapper.toDto(productEntity);
        CACHE.put(productDto.getId(), productDto);
        return productDto;
    }

    public ProductDto update(Long id, ProductRequest request) {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
        productMapper.toEntity(request, productEntity);
        productEntity = productRepository.save(productEntity);
        ProductDto productDto = productMapper.toDto(productEntity);
        CACHE.put(id, productDto);
        return productDto;
    }

    public ProductDto get(Long id) {
        return CACHE.computeIfAbsent(id, this::getFromDb);
    }

    public List<ProductDto> getAll() {
        return productRepository.findAll().stream()
                .map(productMapper::toDto)
                .toList();
    }

    public void delete(Long id) {
        productRepository.deleteById(id);
        CACHE.remove(id);
    }

    public int getCacheSize() {
        return CACHE.size();
    }

    public void clearCache() {
        CACHE.clear();
    }

    private ProductDto getFromDb(Long id) {
        ProductEntity productEntity = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Product not found with id: " + id));
        return productMapper.toDto(productEntity);
    }

}