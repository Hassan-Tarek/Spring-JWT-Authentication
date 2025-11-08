package com.jwt.auth.service;

import com.jwt.auth.dto.request.LoginRequest;
import com.jwt.auth.dto.request.RefreshTokenRequest;
import com.jwt.auth.dto.request.RegisterRequest;
import com.jwt.auth.dto.response.AuthResponse;
import com.jwt.auth.dto.response.UserDto;
import com.jwt.auth.entity.RefreshToken;
import com.jwt.auth.entity.Role;
import com.jwt.auth.entity.User;
import com.jwt.auth.exception.RoleNotFoundException;
import com.jwt.auth.exception.UserAlreadyExistException;
import com.jwt.auth.mapper.UserMapper;
import com.jwt.auth.repository.RoleRepository;
import com.jwt.auth.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final RefreshTokenService refreshTokenService;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    @Autowired
    public AuthService(UserRepository userRepository,
                       UserMapper userMapper,
                       RoleRepository roleRepository,
                       RefreshTokenService refreshTokenService,
                       JwtService jwtService,
                       PasswordEncoder passwordEncoder,
                       AuthenticationManager authenticationManager) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
        this.refreshTokenService = refreshTokenService;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.email())) {
            throw new UserAlreadyExistException("User already exists");
        }

        Role role = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RoleNotFoundException("Role not found"));

        User user = User.builder()
                .firstName(request.firstName())
                .lastName(request.lastName())
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .roles(Set.of(role))
                .build();
        userRepository.save(user);

        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        UserDto userDto = userMapper.toDto(user);
        return new AuthResponse(accessToken, refreshToken.getToken(), userDto);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.email(), request.password())
        );

        User user = (User) authentication.getPrincipal();
        String accessToken = jwtService.generateAccessToken(user);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user);
        UserDto userDto = userMapper.toDto(user);
        return new AuthResponse(accessToken, refreshToken.getToken(), userDto);
    }

    @Transactional
    public AuthResponse refreshToken(RefreshTokenRequest request) {
        RefreshToken oldRefreshToken = refreshTokenService
                .validateRefreshToken(request.refreshToken());

        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(oldRefreshToken);
        User user = newRefreshToken.getUser();
        String newAccessToken = jwtService.generateAccessToken(user);
        UserDto userDto = userMapper.toDto(user);
        return new AuthResponse(newAccessToken, newRefreshToken.getToken(), userDto);
    }

    @Transactional
    public void logout(User user) {
        refreshTokenService.revokeAllUserRefreshTokens(user);
    }

    @Transactional
    public void logout(RefreshTokenRequest request) {
        refreshTokenService.revokeRefreshToken(request.refreshToken());
    }
}
