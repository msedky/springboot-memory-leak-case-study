package org.jvmmemoryleak.case02.common.event;

import org.springframework.context.ApplicationEvent;

import java.time.LocalDate;

public class OrderShippedEvent extends ApplicationEvent {

    private final Long orderId;
    private final String userId;
    private final String shippingAddress;
    private final LocalDate estimatedDelivery;

    public OrderShippedEvent(Object source, Long orderId, String userId,
                             String shippingAddress, LocalDate estimatedDelivery) {
        super(source);
        this.orderId = orderId;
        this.userId = userId;
        this.shippingAddress = shippingAddress;
        this.estimatedDelivery = estimatedDelivery;
    }

    public Long getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
    }

    public String getShippingAddress() {
        return shippingAddress;
    }

    public LocalDate getEstimatedDelivery() {
        return estimatedDelivery;
    }
}