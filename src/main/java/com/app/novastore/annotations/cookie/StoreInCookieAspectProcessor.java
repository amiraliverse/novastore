package com.app.novastore.annotations.cookie;

import com.app.novastore.config.NovastoreProperties;
import com.app.novastore.util.CookieUtils;
import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Aspect
@Component
@AllArgsConstructor
public class StoreInCookieAspectProcessor {

    private final NovastoreProperties properties;

    @AfterReturning(value = "@annotation(storeInCookie)", returning = "result")
    public void putTokenInCookie(JoinPoint joinPoint, StoreInCookie storeInCookie, Object result) {
        String key = StringUtils.hasText(storeInCookie.key())
                ? storeInCookie.key()
                : storeInCookie.value();

        if (!StringUtils.hasText(key))
            return;

        boolean httpOnly = properties.getSecurity().getCookie().getHttpOnly() != null
                ? properties.getSecurity().getCookie().getHttpOnly()
                : storeInCookie.httpOnly();

        boolean secure = properties.getSecurity().getCookie().getSecure() != null
                ? properties.getSecurity().getCookie().getSecure()
                : storeInCookie.secure();

        int maxAge = properties.getSecurity().getCookie().getMaxAge() != null
                ? properties.getSecurity().getCookie().getMaxAge()
                : storeInCookie.maxAge();

        CookieUtils.put(key, result.toString(), httpOnly, secure, maxAge);
    }
}
