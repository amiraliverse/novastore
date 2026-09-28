package com.app.novastore.security;

import com.app.novastore.config.NovastoreProperties;
import com.app.novastore.security.user.PlatformUser;
import com.app.novastore.security.user.type.UserType;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;

import lombok.AllArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;


import java.util.Arrays;
import java.util.Collection;

/**
 * Turns a signed JWT into a Spring Security {@link Authentication}.
 * <p>
 * Split out of {@link SecurityUtils} because it is the only part that needs configuration:
 * the claim names and the signing key arrive by injection, so nothing here reads static
 * global state. {@link SecurityUtils} keeps the methods that only touch
 * {@link SecurityContextHolder} and stays static.
 */
@Component
@AllArgsConstructor
public class JwtAuthenticationResolver {

    private final NovastoreProperties properties;
    private final JwtParser jwtParser;

    public void setAuthenticatedUser(String jwt) {
        SecurityContextHolder.getContext().setAuthentication(toAuthentication(jwt));
    }

    private Authentication toAuthentication(String jwt) {
        Claims claims = jwtParser.parseSignedClaims(jwt).getPayload();

        String stringAuthorities = claims.get(properties.getSecurity().getAuthentication().getJwt().getAuthoritiesKey()).toString();

        Collection<? extends GrantedAuthority> authorities = Arrays
                .stream(stringAuthorities.split(","))
                .filter(auth -> !auth.trim().isEmpty())
                .map(SimpleGrantedAuthority::new)
                .toList();

        String id = claim(claims, properties.getSecurity().getAuthentication().getJwt().getUserIdKey());
        String username = claim(claims, properties.getSecurity().getAuthentication().getJwt().getUsernameKey());
        String rawType = claim(claims, properties.getSecurity().getAuthentication().getJwt().getUserTypeKey());
        UserType type = rawType == null ? null : UserType.valueOf(rawType);
        PlatformUser principal = new PlatformUser(id, username, stringAuthorities, type);

        return new UsernamePasswordAuthenticationToken(principal, jwt, authorities);
    }

    private String claim(Claims claims, String key) {
        Object value = claims.get(key);
        return value == null ? null : String.valueOf(value);
    }
}
