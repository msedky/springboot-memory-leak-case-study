package org.jvmmemoryleak.case03.common.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jvmmemoryleak.case03.common.model.payload.response.ApiResponse;
import org.jvmmemoryleak.case03.common.service.CommonService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/case03/common")
@RequiredArgsConstructor
public class CommonController {

    private final CommonService commonService;

    @GetMapping("/heap")
    public Map<String, Object> heapInfo() {
        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();

        return Map.of(
                "used_mb", usedMemory / (1024 * 1024),
                "free_mb", freeMemory / (1024 * 1024),
                "total_mb", totalMemory / (1024 * 1024),
                "max_mb", maxMemory / (1024 * 1024)
        );
    }

    @PostMapping("/seed")
    public ApiResponse<Map<String, Object>> seed(@RequestParam(defaultValue = "500000") int count) {
        return ApiResponse.ok(commonService.seed(count));
    }
}