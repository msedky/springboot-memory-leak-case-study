package org.jvmmemoryleak.case02.fixed.service;

import lombok.RequiredArgsConstructor;
import org.jvmmemoryleak.case02.common.event.OrderTrackingEvent;
import org.jvmmemoryleak.case02.common.payload.request.TrackingRequest;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FixedTrackingService {

    private final ApplicationEventPublisher eventPublisher;

    public void trackOrder(Long orderId, TrackingRequest request) {
        eventPublisher.publishEvent(
                new OrderTrackingEvent(this, orderId, request.getUserId(), request.getSessionPayload())
        );
    }
}