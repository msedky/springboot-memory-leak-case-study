package org.jvmmemoryleak.case02.common.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/case02/common")
public class CommonController {

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
}
