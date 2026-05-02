package org.jvmmemoryleak.case02.buggy.listener;

import lombok.RequiredArgsConstructor;
import org.jvmmemoryleak.case02.common.event.OrderTrackingEvent;
import org.springframework.context.ApplicationListener;


@RequiredArgsConstructor
public class BuggyOrderTrackingListener implements ApplicationListener<OrderTrackingEvent> {

    private final Long orderId;
    private final String userId;
    private final String sessionPayload;


    @Override
    public void onApplicationEvent(OrderTrackingEvent event) {
        // Simulate checking whether this listener is interested in the event.
        if (this.orderId.equals(event.getOrderId())) {
            // no-op
        }
    }
}