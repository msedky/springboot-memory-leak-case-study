package org.jvmmemoryleak.case01.buggy.controller;

import lombok.RequiredArgsConstructor;
import org.jvmmemoryleak.case01.buggy.service.BuggyProductService;
import org.jvmmemoryleak.case01.common.dto.ProductDto;
import org.jvmmemoryleak.case01.common.payload.request.ProductRequest;
import org.jvmmemoryleak.case01.common.payload.response.ApiResponse;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/case01/buggy/products")
@RequiredArgsConstructor
public class BuggyProductController {

    private final BuggyProductService service;

    @PostMapping
    public ApiResponse<ProductDto> create(@RequestBody ProductRequest request) {
        return ApiResponse.created(service.create(request));
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductDto> update(@PathVariable Long id, @RequestBody ProductRequest request) {
        return ApiResponse.ok(service.update(id, request));
    }

    @GetMapping("/{id}")
    public ApiResponse<ProductDto> get(@PathVariable Long id) {
        return ApiResponse.ok(service.get(id));
    }

    @GetMapping
    public ApiResponse<Iterable<ProductDto>> getAll() {
        return ApiResponse.ok(service.getAll());
    }

    @DeleteMapping("/{id}")
    public ApiResponse delete(@PathVariable Long id) {
        service.delete(id);
        return ApiResponse.ok(null);
    }

    @GetMapping("/cache-size")
    public int cacheSize() {
        return service.getCacheSize();
    }

    @DeleteMapping("/cache")
    public void clearCache() {
        service.clearCache();
    }
}