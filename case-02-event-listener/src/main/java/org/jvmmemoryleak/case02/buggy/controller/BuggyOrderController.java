package org.jvmmemoryleak.case02.buggy.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jvmmemoryleak.case02.common.payload.request.ShipOrderRequest;
import org.jvmmemoryleak.case02.common.payload.response.ApiResponse;
import org.jvmmemoryleak.case02.common.payload.response.ShipOrderResponse;
import org.jvmmemoryleak.case02.common.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/case02/buggy/orders")
@RequiredArgsConstructor
public class BuggyOrderController {

    private final OrderService buggyOrderService;

    @PostMapping("/{orderId}")
    public ApiResponse<ShipOrderResponse> ship(@PathVariable Long orderId,
                                               @Valid @RequestBody ShipOrderRequest request) {
        return ApiResponse.ok(buggyOrderService.shipOrder(orderId, request));
    }
}