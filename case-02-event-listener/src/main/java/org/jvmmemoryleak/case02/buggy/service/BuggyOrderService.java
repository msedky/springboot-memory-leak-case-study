package org.jvmmemoryleak.case02.buggy.service;

import lombok.RequiredArgsConstructor;
import org.jvmmemoryleak.case02.buggy.listener.BuggyOrderShippedListener;
import org.jvmmemoryleak.case02.common.event.OrderShippedEvent;
import org.jvmmemoryleak.case02.common.payload.request.ShipOrderRequest;
import org.jvmmemoryleak.case02.common.payload.response.ShipOrderResponse;
import org.jvmmemoryleak.case02.common.service.OrderService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class BuggyOrderService implements OrderService {

    private final SimpleApplicationEventMulticaster applicationEventMulticaster;
    private final ApplicationEventPublisher eventPublisher;

    public ShipOrderResponse shipOrder(Long orderId, ShipOrderRequest request) {

        // Registers a dedicated listener per ship request
        // 🔥 Bug: listener is never removed — developer didn't think about cleanup
        BuggyOrderShippedListener listener = new BuggyOrderShippedListener(
                orderId,
                request.getUserId(),
                request.getShippingAddress(),
                request.getEstimatedDelivery()
        );

        applicationEventMulticaster.addApplicationListener(listener);

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