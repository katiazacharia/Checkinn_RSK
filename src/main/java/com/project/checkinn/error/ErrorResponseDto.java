package com.project.checkinn.error;

import java.time.LocalDateTime;
import java.util.List;

public class ErrorResponseDto {

    private int status;
    private String message;
    private String path;
    private LocalDateTime timestamp;
    private List<FieldErrorDto> errors;

    public ErrorResponseDto(int status, String message, String path,
                            LocalDateTime timestamp, List<FieldErrorDto> errors) {
        this.status = status;
        this.message = message;
        this.path = path;
        this.timestamp = timestamp;
        this.errors = errors;
    }

    public int getStatus() { return status; }
    public String getMessage() { return message; }
    public String getPath() { return path; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public List<FieldErrorDto> getErrors() { return errors; }
}
