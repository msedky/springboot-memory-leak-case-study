package org.jvmmemoryleak.case01.fixed.controller;

import org.jvmmemoryleak.case01.common.dto.ProductDto;
import org.jvmmemoryleak.case01.fixed.service.FixedProductService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/case01/fixed/products")
public class FixedProductController {

    private final FixedProductService service;

    public FixedProductController(FixedProductService service) {
        this.service = service;
    }

    @GetMapping("/{id}")
    public ProductDto getProduct(@PathVariable Long id) {
        return service.getProduct(id);
    }

    @GetMapping("/cache-size")
    public Long cacheSize() {
        return service.getCacheSize();
    }

    @DeleteMapping("/cache")
    public void clearCache() {
        service.clearCache();
    }
}