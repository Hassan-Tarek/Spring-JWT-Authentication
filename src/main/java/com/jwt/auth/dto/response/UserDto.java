package com.jwt.auth.dto.response;

import java.util.Set;

public record UserDto (
        Long id,
        String firstName,
        String lastName,
        String email,
        Set<String> roles
) { }
