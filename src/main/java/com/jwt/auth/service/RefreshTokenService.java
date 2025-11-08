package com.jwt.auth.service;

import com.jwt.auth.entity.RefreshToken;
import com.jwt.auth.entity.User;
import com.jwt.auth.exception.RefreshTokenException;
import com.jwt.auth.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final JwtService jwtService;

    @Autowired
    public RefreshTokenService(RefreshTokenRepository refreshTokenRepository,
                               JwtService jwtService) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.jwtService = jwtService;
    }

    @Transactional
    public RefreshToken createRefreshToken(User user) {
        String token = jwtService.generateRefreshToken(user);
        RefreshToken refreshToken = RefreshToken.builder()
                .token(token)
                .user(user)
                .expiryDate(Instant.now().plusMillis(jwtService.getRefreshExpiration()))
                .build();
        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional(readOnly = true)
    public RefreshToken validateRefreshToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new RefreshTokenException("Refresh token not found"));

        if (!jwtService.isRefreshToken(token) || !jwtService.isValidToken(token, refreshToken.getUser())) {
            throw new RefreshTokenException("Invalid refresh token");
        }

        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            throw new RefreshTokenException("Refresh token is expired");
        }

        if (refreshToken.getRevoked()) {
            throw new RefreshTokenException("Refresh token is revoked");
        }

        return refreshToken;
    }

    @Transactional
    public void revokeRefreshToken(String token) {
        refreshTokenRepository.findByToken(token)
                .ifPresent(refreshToken -> {
                    refreshToken.setRevoked(true);
                    refreshTokenRepository.save(refreshToken);
                });
    }

    @Transactional
    public void revokeAllUserRefreshTokens(User user) {
        refreshTokenRepository.revokeAllByUserId(user.getId());
    }

    @Transactional
    public RefreshToken rotateRefreshToken(RefreshToken oldRefreshToken) {
        revokeRefreshToken(oldRefreshToken.getToken());
        return createRefreshToken(oldRefreshToken.getUser());
    }
}
