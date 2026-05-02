package org.jvmmemoryleak.case02.buggy.controller;

import lombok.RequiredArgsConstructor;
import org.jvmmemoryleak.case02.buggy.service.BuggyTrackingService;
import org.jvmmemoryleak.case02.common.payload.request.TrackingRequest;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/case02/buggy/tracking")
@RequiredArgsConstructor
public class BuggyTrackingController {

    private final BuggyTrackingService service;

    @PostMapping("/{orderId}")
    public Map<String, Object> track(@PathVariable Long orderId,
                                     @RequestBody TrackingRequest request) {
        int listeners = service.trackOrder(orderId, request);
        return Map.of("orderId", orderId, "registeredListeners", listeners);
    }

    @GetMapping("/listeners-count")
    public Map<String, Object> listenersCount() {
        return Map.of(
                "registeredListeners", service.getRegisteredListenersCount()
        );
    }
}