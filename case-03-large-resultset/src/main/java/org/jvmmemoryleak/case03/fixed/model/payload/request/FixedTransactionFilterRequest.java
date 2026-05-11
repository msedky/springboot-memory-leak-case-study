package org.jvmmemoryleak.case03.fixed.model.payload.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FixedTransactionFilterRequest {
    private LocalDateTime from;
    private LocalDateTime to;
    private int page;
    private int size;
}