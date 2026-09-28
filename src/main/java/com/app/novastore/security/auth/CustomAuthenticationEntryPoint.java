package com.app.novastore.security.auth;

import com.app.novastore.exception.translator.ExceptionTranslator;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@AllArgsConstructor
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final ExceptionTranslator exceptionTranslator;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) throws IOException {
        exceptionTranslator.writeToResponse(request, response, authException);
    }

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response, AccessDeniedException accessDeniedException) throws IOException {
        exceptionTranslator.writeToResponse(request, response, accessDeniedException);
    }
}
