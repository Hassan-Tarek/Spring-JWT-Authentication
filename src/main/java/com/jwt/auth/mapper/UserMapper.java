package com.jwt.auth.mapper;

import com.jwt.auth.dto.response.UserDto;
import com.jwt.auth.entity.User;
import org.springframework.stereotype.Component;

import java.util.stream.Collectors;

@Component
public class UserMapper {

    public UserDto toDto(User user) {
        return new UserDto(
                user.getId(),
                user.getFirstName(),
                user.getLastName(),
                user.getEmail(),
                user.getRoles()
                        .stream()
                        .map(role -> role.getName().replace("ROLE_", ""))
                        .collect(Collectors.toSet())
        );
    }
}
