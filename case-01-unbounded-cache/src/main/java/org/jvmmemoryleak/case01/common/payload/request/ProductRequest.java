package org.jvmmemoryleak.case01.common.payload.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(min = 0, max = 9999999)
    private BigDecimal price;
}