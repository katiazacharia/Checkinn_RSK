package com.project.checkinn.error;

public class FieldErrorDto {

    private String field;
    private String error;

    public FieldErrorDto(String field, String error) {
        this.field = field;
        this.error = error;
    }

    public String getField() { return field; }
    public String getError() { return error; }
}
