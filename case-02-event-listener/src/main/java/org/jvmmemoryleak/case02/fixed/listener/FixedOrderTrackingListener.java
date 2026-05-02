package org.jvmmemoryleak.case02.fixed.listener;

import org.jvmmemoryleak.case02.common.event.OrderTrackingEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Component
public class FixedOrderTrackingListener {

    @EventListener
    public void handle(OrderTrackingEvent event) {
        // One Spring-managed singleton listener.
        // No per-request listener registration.
        // No retained session payload per request.
    }
}