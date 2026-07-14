package com.openfree_api.common.exception;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponse {

    private boolean success;
    private String message;
    private List<String> errors;
    private LocalDateTime timestamp;

    public ErrorResponse(
            boolean success,
            String message,
            List<String> errors,
            LocalDateTime timestamp
    ) {
        this.success = success;
        this.message = message;
        this.errors = errors;
        this.timestamp = timestamp;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        return message;
    }

    public List<String> getErrors() {
        return errors;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }
}