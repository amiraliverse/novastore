package com.app.novastore.security.token.jwt;

import com.app.novastore.annotations.filter.Filter;
import com.app.novastore.config.NovastoreProperties;
import com.app.novastore.security.JwtAuthenticationResolver;
import com.app.novastore.security.token.TokenProvider;
import com.app.novastore.util.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Filter
@AllArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    private final TokenProvider tokenProvider;
    private final NovastoreProperties properties;
    private final JwtAuthenticationResolver jwtAuthenticationResolver;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = JwtUtils.trimBearerPrefix(JwtUtils.resolveJwt(properties.getSecurity().getCookie().getJwtTokenKey()));

        if (JwtUtils.isStructurallyValid(jwt) && tokenProvider.validateToken(jwt)) {
            jwtAuthenticationResolver.setAuthenticatedUser(jwt);
        }

        filterChain.doFilter(request, response);
    }
}
