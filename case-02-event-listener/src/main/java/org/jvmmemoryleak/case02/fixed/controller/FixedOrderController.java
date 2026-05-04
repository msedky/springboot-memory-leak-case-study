package org.jvmmemoryleak.case02.fixed.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.jvmmemoryleak.case02.common.payload.request.ShipOrderRequest;
import org.jvmmemoryleak.case02.common.payload.response.ApiResponse;
import org.jvmmemoryleak.case02.common.payload.response.ShipOrderResponse;
import org.jvmmemoryleak.case02.common.service.OrderService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/case02/fixed/orders")
@RequiredArgsConstructor
public class FixedOrderController {

    private final OrderService fixedOrderService;

    @PostMapping("/{orderId}")
    public ApiResponse<ShipOrderResponse> ship(@PathVariable Long orderId,
                                               @Valid @RequestBody ShipOrderRequest request) {
        return ApiResponse.ok(fixedOrderService.shipOrder(orderId, request));
    }
}