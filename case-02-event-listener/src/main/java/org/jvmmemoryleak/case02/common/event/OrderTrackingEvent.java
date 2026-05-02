package org.jvmmemoryleak.case02.common.event;

import org.springframework.context.ApplicationEvent;

public class OrderTrackingEvent extends ApplicationEvent {

    private final Long orderId;
    private final String userId;
    private final String payload;

    public OrderTrackingEvent(Object source, Long orderId, String userId, String payload) {
        super(source);
        this.orderId = orderId;
        this.userId = userId;
        this.payload = payload;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public String getPayload() {
        return payload;
    }
}