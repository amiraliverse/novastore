package com.app.novastore.security.token;

import com.app.novastore.config.NovastoreProperties;
import com.app.novastore.security.token.invalidation.TokenInvalidationService;
import com.app.novastore.security.token.refresh.RefreshTokenService;
import com.app.novastore.security.user.PlatformUser;
import io.jsonwebtoken.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;


import javax.crypto.SecretKey;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@Slf4j
@Service
public class TokenProvider {

    private final JwtParser jwtParser;

    private final SecretKey signingKey;

    private final NovastoreProperties.Security.Authentication.Jwt jwtProperties;

    private final NovastoreProperties.Security.Authentication.RefreshToken refreshTokenProperties;


    private final RefreshTokenService refreshTokenService;

    private final TokenInvalidationService tokenInvalidationService;

    public TokenProvider(NovastoreProperties properties,
                         JwtParser jwtParser,
                         SecretKey jwtSigningKey,
                         RefreshTokenService refreshTokenService,
                         TokenInvalidationService tokenInvalidationService) {
        this.jwtProperties = properties.getSecurity().getAuthentication().getJwt();
        this.refreshTokenProperties = properties.getSecurity().getAuthentication().getRefreshToken();
        this.jwtParser = jwtParser;
        this.signingKey = jwtSigningKey;
        this.refreshTokenService = refreshTokenService;
        this.tokenInvalidationService = tokenInvalidationService;
    }

    /**
     * A token is valid when it verifies, has not expired, and has not been revoked by a
     * logout or a session deletion.
     */
    public boolean validateToken(String jwt) {
        try {
            jwtParser.parseSignedClaims(jwt).getPayload();

            if (tokenInvalidationService.isInvalidated(jwt)) {
                log.info("Revoked JWT token presented.");
                return false;
            }

            return true;
        } catch (JwtException | IllegalArgumentException e) {
            log.info("Invalid JWT token.");
            log.trace("Invalid JWT token trace.", e);
        }

        return false;
    }

    /**
     * @return the token's expiry, or null when it cannot be read (malformed or already expired)
     */
    public Instant getExpiration(String jwt) {
        try {
            Date expiration = jwtParser.parseSignedClaims(jwt).getPayload().getExpiration();
            return expiration == null ? null : expiration.toInstant();
        } catch (JwtException | IllegalArgumentException e) {
            log.trace("Cannot read expiry of an unparsable token.", e);
            return null;
        }
    }

    public GenericAuthToken createToken(PlatformUser platformUser) {
        return GenericAuthToken.builder()
                .token(generateJwt(platformUser))
                .tokenType(TokenType.BEARER)
                .refreshToken(refreshTokenService.generateByUserId(platformUser.getId(), refreshTokenProperties.getTokenValidityInSeconds()).toString())
                .build();
    }


    private String generateJwt(PlatformUser platformUser) {
        return generateJwt(
                platformUser,
                platformUser.getAuthoritiesAsString(),
                Date.from(Instant.now().plus(jwtProperties.getTokenValidityInSeconds(), ChronoUnit.SECONDS)));
    }

    private String generateJwt(PlatformUser platformUser, String authorities, Date validity) {
        JwtBuilder builder = Jwts
                .builder()
                .id(UUID.randomUUID().toString())
                .subject(platformUser.getFullName())
                .claim(jwtProperties.getAuthoritiesKey(), authorities)
                .claim(jwtProperties.getUserIdKey(), platformUser.getId())
                .claim(jwtProperties.getUserTypeKey(), platformUser.getType())
                .claim(jwtProperties.getUsernameKey(), platformUser.getUsername())
                // Minted here, never taken from the request: a session id echoed back from a
                // client-supplied cookie lets an attacker pin a victim's session to a value
                // they chose, and everything keyed by sid (session lookup, revocation) then
                // trusts that value.
                .claim(jwtProperties.getSessionIdKey(), UUID.randomUUID().toString())
                .claim(jwtProperties.getConsumerKey(), jwtProperties.getConsumerValue())
                .signWith(signingKey, Jwts.SIG.HS512)
                .expiration(validity);

        return builder.compact();
    }

    /**
     * @return the claim value, or null when the token carries no such claim
     */
    public String getClaim(String claim, String jwt) {
        Claims claims = jwtParser.parseSignedClaims(jwt).getPayload();
        Object value = claims.get(claim);
        return value == null ? null : value.toString();
    }
}
