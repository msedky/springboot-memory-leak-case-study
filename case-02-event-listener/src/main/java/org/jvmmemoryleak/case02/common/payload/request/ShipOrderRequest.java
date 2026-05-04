package org.jvmmemoryleak.case02.common.payload.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShipOrderRequest {

    @NotBlank
    private String userId;

    @NotBlank
    private String shippingAddress;

    @NonNull
    private LocalDate estimatedDelivery;
}