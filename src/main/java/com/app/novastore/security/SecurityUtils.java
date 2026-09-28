package com.app.novastore.security;

import com.app.novastore.constants.NovastoreConstants;
import com.app.novastore.security.user.PlatformUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * Read-only access to the current {@link SecurityContextHolder} principal.
 * Deliberately static and configuration-free; anything that needs JWT settings lives in
 * {@link JwtAuthenticationResolver}.
 */
public final class SecurityUtils {

    public static void setAuthenticatedUser(Authentication authentication) {
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    public static PlatformUser getCurrentUser() {
        return extractPrincipalUser(SecurityContextHolder.getContext().getAuthentication());
    }

    public static Optional<String> getCurrentUserJwt() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
                .filter(a -> a.getCredentials() instanceof String)
                .map(a -> (String) a.getCredentials());

    }

    public static String getCurrentUserId() {
        return extractPrincipalUser(SecurityContextHolder.getContext().getAuthentication()).getId();
    }

    public static String getCurrentUserUsername() {
        return extractPrincipalUser(SecurityContextHolder.getContext().getAuthentication()).getUsername();
    }

    /**
     * The single fallback rule for "who did this" across JPA auditing
     * ({@link SpringSecurityAuditorAware}) and native writes that can't rely on it
     * (e.g. {@code TradeLimitUsageRepository.upsertUsage}) - one copy, so both stay in sync.
     */
    public static String getCurrentUsernameOrSystem() {
        return Optional.ofNullable(getCurrentUser())
                .map(PlatformUser::getUsername)
                .orElse(NovastoreConstants.SYSTEM);
    }

    private static PlatformUser extractPrincipalUser(Authentication authentication) {
        if (authentication == null) {
            return null;
        } else if (authentication.getPrincipal() instanceof PlatformUser) {
            return (PlatformUser) authentication.getPrincipal();
        }
        return null;
    }
}
