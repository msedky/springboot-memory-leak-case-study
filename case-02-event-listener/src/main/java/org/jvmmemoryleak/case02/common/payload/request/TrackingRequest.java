package org.jvmmemoryleak.case02.common.payload.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TrackingRequest {
    private String userId;
    private String sessionPayload;
}