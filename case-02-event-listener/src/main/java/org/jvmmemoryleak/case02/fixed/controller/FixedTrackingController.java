package org.jvmmemoryleak.case02.fixed.controller;

import lombok.RequiredArgsConstructor;
import org.jvmmemoryleak.case02.common.payload.request.TrackingRequest;
import org.jvmmemoryleak.case02.fixed.service.FixedTrackingService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/case02/fixed/tracking")
@RequiredArgsConstructor
public class FixedTrackingController {

    private final FixedTrackingService service;

    @PostMapping("/{orderId}")
    public Map<String, Object> track(@PathVariable Long orderId,
                                     @RequestBody TrackingRequest request) {
        service.trackOrder(orderId, request);
        return Map.of("orderId", orderId, "status", "tracked");
    }
}