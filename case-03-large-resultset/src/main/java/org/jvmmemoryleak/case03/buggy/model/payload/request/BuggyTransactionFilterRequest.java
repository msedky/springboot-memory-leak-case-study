package org.jvmmemoryleak.case03.buggy.model.payload.request;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuggyTransactionFilterRequest {
    private LocalDateTime from;
    private LocalDateTime to;
}