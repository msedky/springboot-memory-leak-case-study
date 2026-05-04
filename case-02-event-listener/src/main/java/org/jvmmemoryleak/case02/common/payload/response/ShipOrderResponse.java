package org.jvmmemoryleak.case02.common.payload.response;

import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipOrderResponse {
    private Long orderId;
    private String status;
    private String userId;
    private String shippingAddress;
    private LocalDate estimatedDelivery;
}
