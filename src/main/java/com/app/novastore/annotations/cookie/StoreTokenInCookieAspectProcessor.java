package com.app.novastore.annotations.cookie;

import com.app.novastore.config.NovastoreProperties;
import com.app.novastore.security.token.GenericAuthToken;
import com.app.novastore.util.CookieUtils;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class StoreTokenInCookieAspectProcessor {

    private final NovastoreProperties.Security.Cookie cookieProperties;
    private final NovastoreProperties.Security.Authentication.Jwt jwtProperties;
    private final NovastoreProperties.Security.Authentication.RefreshToken refreshTokenProperties;

    public StoreTokenInCookieAspectProcessor(NovastoreProperties properties) {
        this.cookieProperties = properties.getSecurity().getCookie();
        this.jwtProperties = properties.getSecurity().getAuthentication().getJwt();
        this.refreshTokenProperties = properties.getSecurity().getAuthentication().getRefreshToken();
    }

    @AfterReturning(value = "@annotation(com.app.novastore.annotations.cookie.StoreTokenInCookie)", returning = "result")
    public void putTokenInCookie(JoinPoint joinPoint, Object result) {
        if (result instanceof GenericAuthToken authToken) {
            CookieUtils.putSecure(cookieProperties.getJwtTokenKey(), authToken.getToken(), jwtProperties.getTokenValidityInSeconds().intValue());
            CookieUtils.putSecure(cookieProperties.getRefreshTokenKey(), authToken.getRefreshToken(), refreshTokenProperties.getTokenValidityInSeconds().intValue());
        }
    }
}
