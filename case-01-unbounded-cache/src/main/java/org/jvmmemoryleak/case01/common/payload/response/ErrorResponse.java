package org.jvmmemoryleak.case01.common.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;
import java.util.List;

@Getter
@Setter
@Builder
public class ErrorResponse {
    private boolean success;
    private int status;
    private String message;
    private List<String> errors;
    private OffsetDateTime timestamp;

    public static ErrorResponse of(int status, String message, List<String> errors) {
        return ErrorResponse.builder()
                .success(false)
                .status(status)
                .message(message)
                .errors(errors)
                .timestamp(OffsetDateTime.now())
                .build();
    }
}
