package com.app.novastore.security.token.invalidation;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.Instant;
import java.util.HexFormat;

/**
 * Deny-list for JWTs revoked before their natural expiry.
 * <p>
 * Stateless JWT auth has no way to withdraw a token once issued, so logout and session
 * revocation would otherwise be cosmetic: the token keeps working for the rest of its 30-day
 * validity. Every entry carries a TTL equal to the token's own remaining life, which bounds
 * this keyspace to the tokens currently in circulation.
 */
@Slf4j
@Service
@AllArgsConstructor
public class TokenInvalidationService {

    private static final String DIGEST_ALGORITHM = "SHA-256";

    private final InvalidatedTokenRepository repository;

    /**
     * Revokes a token until it expires on its own. A token already past {@code expiresAt} is
     * ignored - signature validation rejects it without help from Redis.
     */
    public void invalidate(String jwt, String userId, Instant expiresAt) {
        if (!StringUtils.hasText(jwt) || expiresAt == null) {
            return;
        }

        long remainingSeconds = expiresAt.getEpochSecond() - Instant.now().getEpochSecond();
        if (remainingSeconds <= 0) {
            return;
        }

        repository.save(new InvalidatedToken(digest(jwt), userId, remainingSeconds));
    }

    public boolean isInvalidated(String jwt) {
        return StringUtils.hasText(jwt) && repository.existsById(digest(jwt));
    }

    private String digest(String jwt) {
        try {
            MessageDigest messageDigest = MessageDigest.getInstance(DIGEST_ALGORITHM);
            return HexFormat.of().formatHex(messageDigest.digest(jwt.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(DIGEST_ALGORITHM + " is required to revoke tokens", e);
        }
    }
}
