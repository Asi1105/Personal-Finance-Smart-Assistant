package com.sydney.uni.backend.dto;

import java.util.Map;

public class ErrorResponse {
    private String message;
    private String code;
    private Map<String, Object> details;

    public ErrorResponse() {}

    public ErrorResponse(String message, String code, Map<String, Object> details) {
        this.message = message;
        this.code = code;
        this.details = details;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
}


