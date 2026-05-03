package org.jvmmemoryleak.case01.common.payload.request;

import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductRequest {

    @NotBlank
    @Size(max = 50)
    private String name;
    @NotBlank
    @Size(max = 200_000)
    private String description;

    @NotNull
    @DecimalMin("0.00")
    @DecimalMax("9999999.99")
    private BigDecimal price;
}