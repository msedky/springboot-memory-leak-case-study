package org.jvmmemoryleak.case02.buggy.service;

import lombok.RequiredArgsConstructor;
import org.jvmmemoryleak.case02.buggy.listener.BuggyOrderTrackingListener;
import org.jvmmemoryleak.case02.common.event.OrderTrackingEvent;
import org.jvmmemoryleak.case02.common.payload.request.TrackingRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.event.SimpleApplicationEventMulticaster;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
public class BuggyTrackingService {

    private final SimpleApplicationEventMulticaster applicationEventMulticaster;
    private final ApplicationEventPublisher eventPublisher;

    private final AtomicInteger registeredListeners = new AtomicInteger();

    public int trackOrder(Long orderId, TrackingRequest request) {
        BuggyOrderTrackingListener listener =
                new BuggyOrderTrackingListener(orderId, request.getUserId(), request.getSessionPayload());

        applicationEventMulticaster.addApplicationListener(listener);
        registeredListeners.incrementAndGet();

        eventPublisher.publishEvent(
                new OrderTrackingEvent(this, orderId, request.getUserId(), request.getSessionPayload())
        );

        return registeredListeners.get();
    }

    public int getRegisteredListenersCount() {
        return registeredListeners.get();
    }
}