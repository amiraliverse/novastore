package com.app.novastore.security.token.refresh;

import com.app.novastore.config.NovastoreProperties;
import com.app.novastore.security.SecurityUtils;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
public class RefreshTokenService {

    private final RefreshTokenRepository repository;
    private final NovastoreProperties.Security.Authentication.RefreshToken refreshTokenProperties;

    public RefreshTokenService(RefreshTokenRepository repository, NovastoreProperties properties) {
        this.repository = repository;
        this.refreshTokenProperties = properties.getSecurity().getAuthentication().getRefreshToken();
    }

    /**
     * Generates a refresh token with the configured default TTL (Time-To-Live).
     * The token is associated with the provided user ID.
     *
     * @param userId the user ID used as the Redis hash ID.
     * @return the generated refresh token for the specified user ID.
     */
    public RefreshToken generateByUserId(String userId) {
        return generateByUserId(userId, refreshTokenProperties.getTokenValidityInSeconds());
    }

    /**
     * Generates a refresh token with a specified expiration time.
     * The token is associated with the provided user ID.
     *
     * @param userId    the user ID used as the Redis hash ID.
     * @param expiresAt the specific expiration time for this token.
     * @return the generated refresh token for the specified user ID with a custom expiration time.
     */
    public RefreshToken generateByUserId(String userId, Instant expiresAt) {
        Instant now = Instant.now();
        if (!now.isBefore(expiresAt)) {
            throw new IllegalArgumentException("Refresh token expiry must be in the future");
        }
        return repository.save(RefreshToken.forUserId(userId, ChronoUnit.SECONDS.between(now, expiresAt)));
    }

    /**
     * Generates a refresh token with a specified expiration time.
     * The token is associated with the provided user ID.
     *
     * @param userId     the user ID used as the Redis hash ID.
     * @param timeToLive refresh token TTL
     * @return the generated refresh token for the specified user ID with a custom expiration time.
     */
    public RefreshToken generateByUserId(String userId, Long timeToLive) {
        return repository.save(RefreshToken.forUserId(userId, timeToLive));
    }

    /**
     * Generates a refresh token for the currently authenticated user.
     * The token will have a TTL based on the configuration value for token validity in seconds.
     *
     * @return the generated refresh token for the current user.
     */
    public RefreshToken generateForCurrentUser() {
        return generateByUserId(SecurityUtils.getCurrentUserId());
    }

    public Optional<RefreshToken> findById(String id) {
        return repository.findById(id);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }
}
