package com.jwt.auth.dto.response;

public record ErrorResponse (
        String message,
        int status,
        Long timestamp
) { }
