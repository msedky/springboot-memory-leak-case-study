package org.jvmmemoryleak.case02.buggy.listener;

import lombok.extern.slf4j.Slf4j;
import org.jvmmemoryleak.case02.common.event.OrderShippedEvent;
import org.springframework.context.ApplicationListener;

import java.time.LocalDate;

@Slf4j
public class BuggyOrderShippedListener implements ApplicationListener<OrderShippedEvent> {

    // 🔥 Per-request data retained in memory for the lifetime of this listener
    // In production this could be HttpSession, SecurityContext, user preferences, etc.
    private final Long trackedOrderId;
    private final String userId;
    private final String shippingAddress;
    private final LocalDate estimatedDelivery;

    public BuggyOrderShippedListener(Long trackedOrderId, String userId,
                                     String shippingAddress, LocalDate estimatedDelivery) {
        this.trackedOrderId = trackedOrderId;
        this.userId = userId;
        this.shippingAddress = shippingAddress;
        this.estimatedDelivery = estimatedDelivery;
    }

    @Override
    public void onApplicationEvent(OrderShippedEvent event) {
        if (this.trackedOrderId.equals(event.getOrderId())) {
            log.info("Order {} shipped to {} — estimated delivery: {}",
                    event.getOrderId(), shippingAddress, estimatedDelivery);
            // 🔥 Bug: listener is never removed after handling the event
            // In production: should call multicaster.removeApplicationListener(this) here
        }
    }
}