package org.jvmmemoryleak.case02.fixed.service;

import lombok.RequiredArgsConstructor;
import org.jvmmemoryleak.case02.common.event.OrderShippedEvent;
import org.jvmmemoryleak.case02.common.payload.request.ShipOrderRequest;
import org.jvmmemoryleak.case02.common.payload.response.ShipOrderResponse;
import org.jvmmemoryleak.case02.common.service.OrderService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FixedOrderService implements OrderService {

    private final ApplicationEventPublisher eventPublisher;

    public ShipOrderResponse shipOrder(Long orderId, ShipOrderRequest request) {

        // ✅ Same business logic as buggy — publish event
        // ✅ Fix: no listener registration here at all
        // Spring's singleton @EventListener handles all events automatically
        eventPublisher.publishEvent(
                new OrderShippedEvent(this, orderId, request.getUserId(),
                        request.getShippingAddress(), request.getEstimatedDelivery())
        );
        return ShipOrderResponse.builder()
                .orderId(orderId)
                .status("SHIPPED")
                .userId(request.getUserId())
                .shippingAddress(request.getShippingAddress())
                .estimatedDelivery(request.getEstimatedDelivery())
                .build();
    }
}