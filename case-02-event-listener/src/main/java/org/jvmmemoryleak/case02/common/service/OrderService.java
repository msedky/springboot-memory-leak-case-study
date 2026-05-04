package org.jvmmemoryleak.case02.common.service;

import org.jvmmemoryleak.case02.common.payload.request.ShipOrderRequest;
import org.jvmmemoryleak.case02.common.payload.response.ShipOrderResponse;

public interface OrderService {
    ShipOrderResponse shipOrder(Long orderId, ShipOrderRequest request);
}