package org.jvmmemoryleak.case02.common.payload.response;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.OffsetDateTime;

@Getter
@Setter
@Builder
public class ApiResponse<T> {
    private boolean success;
    private T data;
    private String message;
    private int status;
    private OffsetDateTime timestamp;

    public static <T> ApiResponse<T> success(T data, String message, int status) {
        return ApiResponse.<T>builder().
                data(data)
                .message(message)
                .status(status)
                .success(true)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> created(T data) {
        return ApiResponse.<T>builder().
                data(data)
                .message("Created Successfully")
                .status(HttpStatus.CREATED.value())
                .success(true)
                .timestamp(OffsetDateTime.now())
                .build();
    }

    public static <T> ApiResponse<T> ok(T data) {
        return ApiResponse.<T>builder().
                data(data)
                .message("OK")
                .status(HttpStatus.OK.value())
                .success(true)
                .timestamp(OffsetDateTime.now())
                .build();
    }
}