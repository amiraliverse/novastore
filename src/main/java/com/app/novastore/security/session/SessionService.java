package com.app.novastore.security.session;

import com.app.novastore.config.NovastoreProperties;
import com.app.novastore.security.SecurityUtils;
import com.app.novastore.security.token.GenericAuthToken;
import com.app.novastore.security.token.TokenProvider;
import com.app.novastore.security.token.invalidation.TokenInvalidationService;
import com.app.novastore.security.token.refresh.RefreshTokenService;
import com.app.novastore.security.user.PlatformUser;
import com.app.novastore.util.HttpHelperUtils;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class SessionService {

    private final SessionRepository repository;
    private final TokenProvider tokenProvider;
    private final TokenInvalidationService tokenInvalidationService;
    private final RefreshTokenService refreshTokenService;
    private final NovastoreProperties properties;

    public Optional<Session> findById(String id) {
        return repository.findById(id);
    }

    public Optional<Session> findByUserIdAndIp(String userId, String ip) {
        return repository.findByUserIdAndIp(userId, ip);
    }

    public List<Session> findAllByUserId(String userId) {
        return repository.findAllByUserId(userId);
    }

    public List<Session> findAllForCurrentUser() {
        Optional<String> currentToken = SecurityUtils.getCurrentUserJwt();

        return repository.findAllByUserId(SecurityUtils.getCurrentUserId())
                .stream()
                .peek(session -> session.setIsCurrentDevice(
                        currentToken
                                .map(token -> Optional.ofNullable(tokenProvider.getClaim(sessionIdKey(), token))
                                        .filter(claim -> claim.contentEquals(session.getId()))
                                        .isPresent())
                                .orElse(false)
                ))
                .sorted(Comparator.comparing(Session::getDate).reversed())
                .toList();
    }


    public Session persist(Session session) {
        return repository.save(session);
    }

    public Session buildSession(GenericAuthToken token, PlatformUser platformUser) {
        HttpHelperUtils.ClientDeviceSpec deviceSpec = HttpHelperUtils.getClientDeviceSpecifications();
        return new Session(
                tokenProvider.getClaim(sessionIdKey(), token.getIdToken()),
                platformUser.getId(),
                HttpHelperUtils.getClientIpAddressIfServletRequestExist(),
                deviceSpec.browserType(),
                deviceSpec.browserName(),
                deviceSpec.operatingSystemType(),
                deviceSpec.operatingSystemName(),
                token.getToken(),
                properties.getSecurity().getAuthentication().getJwt().getTokenValidityInSeconds(),
                token.getTokenType(),
                token.getRefreshToken(),
                new Date(),
                null);
    }

    public void deleteById(String id) {
        repository.deleteById(id);
    }

    /**
     * Revokes one of the current user's sessions: the session record goes, its JWT lands on
     * the deny-list for the rest of its lifetime, and its refresh token is dropped so it
     * cannot mint a replacement.
     * <p>
     * Ownership is checked against the authenticated principal rather than trusted from the
     * path variable - session ids are guessable enough that skipping this turns the endpoint
     * into "log out any user you can name".
     *
     * @return true when a session was revoked, false when the id is unknown or not the
     * caller's - the two are deliberately indistinguishable to the client
     */
    public boolean deleteByIdAndMakeTokenInvalid(String id) {
        String currentUserId = SecurityUtils.getCurrentUserId();

        return findById(id)
                .filter(session -> session.getUserId() != null && session.getUserId().equals(currentUserId))
                .map(session -> {
                    revoke(session);
                    return true;
                })
                .orElse(false);
    }

    /**
     * Revokes the session the caller is currently authenticated with. Used by logout, which
     * has the token in hand but not the session id.
     */
    public void revokeCurrentSession(String jwt) {
        Optional.ofNullable(tokenProvider.getClaim(sessionIdKey(), jwt))
                .flatMap(this::findById)
                .ifPresentOrElse(
                        this::revoke,
                        // No session record (already revoked, or Redis lost it): deny-list the
                        // presented token anyway so logout is never a no-op.
                        () -> tokenInvalidationService.invalidate(
                                jwt, SecurityUtils.getCurrentUserId(), tokenProvider.getExpiration(jwt)));
    }

    private void revoke(Session session) {
        Optional.ofNullable(session.getIdToken())
                .ifPresent(token -> tokenInvalidationService.invalidate(
                        token, session.getUserId(), tokenProvider.getExpiration(token)));

        Optional.ofNullable(session.getRefreshToken())
                .ifPresent(refreshTokenService::deleteById);

        deleteById(session.getId());
    }

    private String sessionIdKey() {
        return properties.getSecurity().getAuthentication().getJwt().getSessionIdKey();
    }
}
