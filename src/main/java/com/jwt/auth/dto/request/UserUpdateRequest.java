package com.jwt.auth.dto.request;

import jakarta.validation.constraints.Size;

public record UserUpdateRequest(
        @Size(min = 2, max = 50, message = "First name must be between 2 and 50 characters")
        String firstName,

        @Size(min = 2, max = 50, message = "Last name must be between 2 and 50 characters")
        String lastName,

        @Size(min = 6, message = "Password must be at least 6 characters")
        String password
) { }
