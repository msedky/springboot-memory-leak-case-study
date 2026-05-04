package org.jvmmemoryleak.case02.fixed.listener;

import lombok.extern.slf4j.Slf4j;
import org.jvmmemoryleak.case02.common.event.OrderShippedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class FixedOrderShippedListener {

    // ✅ One singleton listener managed by Spring
    // Handles ALL OrderShippedEvents — no per-request registration
    // No per-request data retained — nothing accumulates in memory
    @EventListener
    public void handle(OrderShippedEvent event) {
        log.info("Order {} shipped to {} — estimated delivery: {}",
                event.getOrderId(), event.getShippingAddress(), event.getEstimatedDelivery());
    }
}