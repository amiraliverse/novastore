package com.app.novastore.util;

import org.springframework.http.HttpHeaders;
import org.springframework.util.StringUtils;

public final class JwtUtils {

    /**
     * Checks if a JWT token has a valid structure (three parts and Base64 encoding).
     *
     * @param token the JWT token string
     * @return true if the structure is valid; false otherwise
     */
    public static boolean isStructurallyValid(String token) {
        if (!StringUtils.hasText(token))
            return false;

        String[] parts = token.split("\\.");
        return parts.length == 3;
    }

    /**
     * Resolves the caller's JWT: the cookie written at login first, then the
     * {@code Authorization} header as a fallback for non-browser clients.
     * <p>
     * <strong>Why the order matters.</strong> A cookie is ambient credential: the browser
     * attaches it to any request that reaches this origin, including one triggered by a page
     * the user did not intend to act from. That is exactly what CSRF exploits, and this API
     * runs with {@code csrf(disable)}, so no token check stands behind it. What holds the
     * line here is {@code SameSite=Strict} on the cookie (see
     * {@link CookieUtils#put(String, String, boolean, boolean, int)}): the browser refuses to
     * send it on any cross-site request, so a third-party page cannot ride the session.
     * <p>
     * Residual risk this accepts, in exchange for the cookie flow:
     * <ul>
     *   <li>{@code SameSite} is same-<em>site</em>, not same-origin. A sibling subdomain that
     *       can be taken over (or an XSS on one) is still same-site and can forge requests.</li>
     *   <li>Clients that ignore {@code SameSite} - old browsers, non-browser HTTP libraries
     *       replaying a stored cookie jar - lose the protection entirely.</li>
     * </ul>
     * If either becomes a real exposure, the fix is a CSRF token on state-changing routes or a
     * {@code __Host-} prefixed cookie, not re-ordering these two lookups.
     *
     * @param cookieName name of the cookie the login flow stores the token in
     * @return the raw credential (possibly still {@code Bearer }-prefixed), or null when absent
     */
    public static String resolveJwt(String cookieName) {
        String fromCookie = CookieUtils.get(cookieName);
        if (StringUtils.hasText(fromCookie)) {
            return fromCookie;
        }

        return HttpHelperUtils.getRequestIfPossible()
                .map(request -> request.getHeader(HttpHeaders.AUTHORIZATION))
                .filter(StringUtils::hasText)
                .orElse(null);
    }

    /**
     * Strips a leading 'Bearer ' scheme from the header value. Only the prefix is
     * removed - the literal can legitimately occur inside the token body.
     *
     * @param token raw Authorization header value
     * @return the bare token, or an empty string when there is nothing to trim
     */
    public static String trimBearerPrefix(String token) {
        if (!StringUtils.hasText(token))
            return "";
        return token.replaceFirst("^Bearer\\s+", "");
    }
}
