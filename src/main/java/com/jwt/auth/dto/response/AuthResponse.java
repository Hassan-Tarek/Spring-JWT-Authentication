package com.jwt.auth.dto.response;

public record AuthResponse (
        String accessToken,
        String refreshToken,
        String tokenType,
        UserDto userDto
) {
    public AuthResponse(String accessToken, String refreshToken, UserDto userDto) {
        this(accessToken, refreshToken, "Bearer", userDto);
    }
}
