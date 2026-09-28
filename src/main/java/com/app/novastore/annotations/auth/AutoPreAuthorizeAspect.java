package com.app.novastore.annotations.auth;

import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Aspect
@Component
public class AutoPreAuthorizeAspect {

    private final ExpressionParser parser = new SpelExpressionParser();

    @Before("@annotation(autoPreAuthorize)")
    public void checkAuthority(JoinPoint joinPoint, AutoPreAuthorize autoPreAuthorize) throws Throwable {
        Method method = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Class<?> controllerClass = method.getDeclaringClass();

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new AccessDeniedException("Unauthenticated");
        }

        List<String> fullPath = resolveFullPath(controllerClass, method);

        StandardEvaluationContext ctx = new StandardEvaluationContext(new AutoPreAuthorizeHelper(fullPath));
        ctx.setVariable("authentication", authentication);
        ctx.setVariable("principal", authentication.getPrincipal());
        ctx.setVariable("args", joinPoint.getArgs());

        String value;
        if (autoPreAuthorize.value().isEmpty()) {
            value = String.format("hasAuthority(%s)", toMethodParameters(resolveFullPath(controllerClass, method)));
        } else {
            value = autoPreAuthorize.value();
        }

        Boolean result = parser.parseExpression(value).getValue(ctx, Boolean.class);

        if (Boolean.FALSE.equals(result)) {
            throw new AccessDeniedException("Forbidden");
        }
    }

    private String toMethodParameters(List<String> a) {
        return a.stream()
                .map(str -> "'" + str + "'")
                .collect(Collectors.joining(","));
    }

    private List<String> resolveFullPath(Class<?> controllerClass, Method targetMethod) {
        List<String> allPaths = new ArrayList<>();
        for (String base : resolveBasePaths(controllerClass)) {
            for (String methodPath : resolveMethodPaths(targetMethod)) {
                String full = (base + methodPath)
                        .replaceAll("//+", "/")
                        .replaceFirst("^/api/", "")
                        .replaceAll("^/", "")
                        .replaceAll("/", ".")
                        .replaceAll("\\{.*?}", "*");
                allPaths.add(full);
            }
        }
        return allPaths;
    }

    private List<String> resolveBasePaths(Class<?> controllerClass) {
        if (controllerClass.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping rm = controllerClass.getAnnotation(RequestMapping.class);
            return pickNonEmpty(rm.path(), rm.value());
        }
        return Collections.singletonList("");
    }

    private List<String> resolveMethodPaths(Method method) {
        if (method.isAnnotationPresent(GetMapping.class))
            return pickNonEmpty(method.getAnnotation(GetMapping.class).path(), method.getAnnotation(GetMapping.class).value());
        if (method.isAnnotationPresent(PostMapping.class))
            return pickNonEmpty(method.getAnnotation(PostMapping.class).path(), method.getAnnotation(PostMapping.class).value());
        if (method.isAnnotationPresent(PutMapping.class))
            return pickNonEmpty(method.getAnnotation(PutMapping.class).path(), method.getAnnotation(PutMapping.class).value());
        if (method.isAnnotationPresent(DeleteMapping.class))
            return pickNonEmpty(method.getAnnotation(DeleteMapping.class).path(), method.getAnnotation(DeleteMapping.class).value());
        if (method.isAnnotationPresent(RequestMapping.class))
            return pickNonEmpty(method.getAnnotation(RequestMapping.class).path(), method.getAnnotation(RequestMapping.class).value());
        return Collections.singletonList("");
    }

    private List<String> pickNonEmpty(String[] primary, String[] fallback) {
        if (primary != null && primary.length > 0 && !isAllEmpty(primary)) return Arrays.asList(primary);
        if (fallback != null && fallback.length > 0 && !isAllEmpty(fallback)) return Arrays.asList(fallback);
        return Collections.singletonList("");
    }

    private boolean isAllEmpty(String[] arr) {
        for (String s : arr) if (!s.isEmpty()) return false;
        return true;
    }

    @AllArgsConstructor
    public static class AutoPreAuthorizeHelper {

        private final List<String> fullPath;

        public boolean hasAuthority(String... authorities) {
            Set<String> methodAuthorities = new HashSet<>(fullPath);
            methodAuthorities.addAll(Stream.of(authorities).map(String::toLowerCase).toList());

            return SecurityContextHolder.getContext().getAuthentication().getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(String::toLowerCase)
                    .anyMatch(methodAuthorities::contains);
        }
    }

}
