package com.app.novastore.util;

import jakarta.servlet.http.Cookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public final class CookieUtils {

    private static final String SAME_SITE_STRICT = "Strict";

    public static String get(String cookieName) {
        return HttpHelperUtils.getRequestIfPossible()
                .filter(request -> request.getCookies() != null)
                .flatMap(request -> Arrays.stream(request.getCookies())
                        .filter(cookie -> cookieName.equals(cookie.getName()))
                        .map(Cookie::getValue)
                        .findFirst())
                .orElse(null);
    }

    public static String find(String cookieName) {
        return Optional.ofNullable(get(cookieName))
                .orElseGet(() -> findInResponse(cookieName));
    }

    /**
     * Writes the Set-Cookie header by hand: {@link Cookie} has no SameSite setter in this
     * servlet version, and every cookie this service issues must be SameSite=Strict.
     * A negative maxAge produces a session cookie (no Max-Age attribute).
     */
    public static void put(String cookieName, String value, boolean httpOnly, boolean secure, int maxAge) {
        HttpHelperUtils.getResponseIfPossible().ifPresent(response ->
                response.addHeader(HttpHeaders.SET_COOKIE,
                        ResponseCookie.from(cookieName, value)
                                .httpOnly(httpOnly)
                                .secure(secure)
                                .path("/")
                                .maxAge(maxAge)
                                .sameSite(SAME_SITE_STRICT)
                                .build()
                                .toString()));
    }

    public static void putSecure(String cookieName, String value, int maxAge) {
        put(cookieName, value, true, true, maxAge);
    }

    /**
     * Expires a cookie in the browser. Attributes must match the ones it was written with,
     * otherwise the browser keeps the original.
     */
    public static void remove(String cookieName) {
        put(cookieName, "", true, true, 0);
    }

    private static String findInResponse(String cookieName) {
        return HttpHelperUtils.getResponseIfPossible()
                .map(response -> response.getHeaders(HttpHeaders.SET_COOKIE))
                .flatMap(headers -> headers.stream()
                        .map(header -> parseCookieValue(header, cookieName))
                        .filter(Objects::nonNull)
                        .findFirst())
                .orElse(null);
    }

    private static String parseCookieValue(String header, String cookieName) {
        return Arrays.stream(header.split(";"))
                .map(String::trim)
                .filter(part -> part.startsWith(cookieName + "="))
                .map(part -> part.substring(cookieName.length() + 1))
                .findFirst()
                .orElse(null);
    }
}
