package com.app.novastore.exception.translator;

import com.app.novastore.constants.NovastoreConstants;
import com.app.novastore.errors.AppErrorCodes;
import com.app.novastore.exception.problem.RestProblem;
import tools.jackson.databind.json.JsonMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.validation.BindException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Slf4j
@RestControllerAdvice
@AllArgsConstructor
public class ExceptionTranslator {

    private final Environment environment;
    private final JsonMapper objectMapper;

    @ExceptionHandler(Exception.class)
    public ResponseEntity<RestProblem> handleAllException(HttpServletRequest request, Exception ex) {
        return buildProblemResponseEntity(request, ex);
    }

    @NotNull
    private ResponseEntity<RestProblem> buildProblemResponseEntity(HttpServletRequest request, Exception ex) {
        RestProblem restProblem = null;
        log.error("Unhandled exception on {} {}", request.getMethod(), request.getRequestURI(), ex);
        if (ex instanceof RestProblem e) {
            e.setPath(request.getContextPath());
            restProblem = e;
        } else if (ex instanceof HttpMediaTypeNotSupportedException) {
            restProblem = new RestProblem(
                    NovastoreConstants.DEFAULT_URL,
                    "نوع رسانه پشتیبانی نمی‌شود",
                    "unsupported media type",
                    "415",
                    HttpStatus.UNSUPPORTED_MEDIA_TYPE,
                    request.getContextPath()
            );
        } else if (ex instanceof HttpRequestMethodNotSupportedException) {
            restProblem = new RestProblem(
                    NovastoreConstants.DEFAULT_URL,
                    "متد غیرمجاز است",
                    "method not allowed",
                    "405",
                    HttpStatus.METHOD_NOT_ALLOWED,
                    request.getContextPath()
            );
        } else if (ex instanceof NoResourceFoundException) {
            restProblem = new RestProblem(
                    NovastoreConstants.DEFAULT_URL,
                    "یافت نشد",
                    "not found",
                    "404",
                    HttpStatus.NOT_FOUND,
                    request.getContextPath()
            );
        } else if (ex instanceof DataIntegrityViolationException) {
            restProblem = new RestProblem(
                    NovastoreConstants.DEFAULT_URL,
                    AppErrorCodes.DATABASE_CONSTRAINTS_VIOLATED.getPersianMessage(),
                    AppErrorCodes.DATABASE_CONSTRAINTS_VIOLATED.getEnglishMessage() + (isProd() ? "" : ": " + ex.getMessage()),
                    AppErrorCodes.DATABASE_CONSTRAINTS_VIOLATED.getCode(),
                    AppErrorCodes.DATABASE_CONSTRAINTS_VIOLATED.getStatus(),
                    request.getContextPath()
            );
        } else if (ex instanceof ConstraintViolationException e) {
            String englishMessage = AppErrorCodes.CONSTRAINT_VALIDATION.getEnglishMessage();

            if (!isProd()) {
                List<String> errors = e.getConstraintViolations()
                        .stream()
                        .map(violation -> violation.getPropertyPath() + ": " + violation.getMessage())
                        .toList();

                englishMessage += " | Errors: " + String.join(", ", errors);
            }

            restProblem = new RestProblem(
                    NovastoreConstants.DEFAULT_URL,
                    AppErrorCodes.CONSTRAINT_VALIDATION.getPersianMessage(),
                    englishMessage,
                    AppErrorCodes.CONSTRAINT_VALIDATION.getCode(),
                    AppErrorCodes.CONSTRAINT_VALIDATION.getStatus(),
                    request.getContextPath()
            );
        } else if (ex instanceof MethodArgumentNotValidException e) {
            String englishMessage = AppErrorCodes.CONSTRAINT_VALIDATION.getEnglishMessage();

            if (!isProd()) {
                List<String> errors = e.getBindingResult()
                        .getFieldErrors()
                        .stream()
                        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .toList();

                englishMessage += " | Errors: " + String.join(", ", errors);
            }

            restProblem = new RestProblem(
                    NovastoreConstants.DEFAULT_URL,
                    AppErrorCodes.CONSTRAINT_VALIDATION.getPersianMessage(),
                    englishMessage,
                    AppErrorCodes.CONSTRAINT_VALIDATION.getCode(),
                    AppErrorCodes.CONSTRAINT_VALIDATION.getStatus(),
                    request.getContextPath()
            );
        } else if (ex instanceof HandlerMethodValidationException e) {
            String englishMessage = AppErrorCodes.CONSTRAINT_VALIDATION.getEnglishMessage();

            if (!isProd()) {
                List<String> errors = e.getParameterValidationResults()
                        .stream()
                        .flatMap(result -> result.getResolvableErrors().stream())
                        .map(error -> {
                            if (error.getCodes() != null && error.getCodes().length > 0) {
                                String[] codes = error.getCodes()[0].split("\\.");
                                return codes.length > 2 ? codes[2] + ": " + error.getDefaultMessage() : error.getDefaultMessage();
                            }
                            return null;
                        })
                        .filter(Objects::nonNull)
                        .toList();

                englishMessage += " | Errors: " + String.join(", ", errors);
            }

            restProblem = new RestProblem(
                    NovastoreConstants.DEFAULT_URL,
                    AppErrorCodes.CONSTRAINT_VALIDATION.getPersianMessage(),
                    englishMessage,
                    AppErrorCodes.CONSTRAINT_VALIDATION.getCode(),
                    AppErrorCodes.CONSTRAINT_VALIDATION.getStatus(),
                    request.getContextPath()
            );
        } else if (ex instanceof BindException e) {
            String englishMessage = AppErrorCodes.CONSTRAINT_VALIDATION.getEnglishMessage();

            if (!isProd()) {
                List<String> errors = e.getFieldErrors()
                        .stream()
                        .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                        .toList();

                englishMessage += " | Errors: " + String.join(", ", errors);
            }

            restProblem = new RestProblem(
                    NovastoreConstants.DEFAULT_URL,
                    AppErrorCodes.CONSTRAINT_VALIDATION.getPersianMessage(),
                    englishMessage,
                    AppErrorCodes.CONSTRAINT_VALIDATION.getCode(),
                    AppErrorCodes.CONSTRAINT_VALIDATION.getStatus(),
                    request.getContextPath()
            );
        } else if (ex instanceof MissingServletRequestParameterException e) {
            restProblem = new RestProblem(
                    NovastoreConstants.DEFAULT_URL,
                    "پارامتر مورد نیاز یافت نشد",
                    "missing request parameter: " + e.getParameterName(),
                    "400",
                    HttpStatus.BAD_REQUEST,
                    request.getContextPath()
            );
        } else if (ex instanceof HttpMessageNotReadableException e) {
            String englishMessage = AppErrorCodes.MALFORMED_REQUEST.getEnglishMessage();

            if (!isProd()) {
                Throwable cause = e.getMostSpecificCause();
                if (cause != null) {
                    englishMessage += ": " + cause.getMessage();
                }
            }

            restProblem = new RestProblem(
                    NovastoreConstants.DEFAULT_URL,
                    AppErrorCodes.MALFORMED_REQUEST.getPersianMessage(),
                    englishMessage,
                    AppErrorCodes.MALFORMED_REQUEST.getCode(),
                    AppErrorCodes.MALFORMED_REQUEST.getStatus(),
                    request.getContextPath()
            );
        } else if (ex instanceof AccessDeniedException) {
            restProblem = new RestProblem(
                    NovastoreConstants.DEFAULT_URL,
                    AppErrorCodes.FORBIDDEN.getPersianMessage(),
                    AppErrorCodes.FORBIDDEN.getEnglishMessage(),
                    AppErrorCodes.FORBIDDEN.getCode(),
                    AppErrorCodes.FORBIDDEN.getStatus(),
                    request.getContextPath()
            );
        } else if (ex instanceof AuthenticationException) {
            restProblem = new RestProblem(
                    NovastoreConstants.DEFAULT_URL,
                    AppErrorCodes.UNAUTHORIZED.getPersianMessage(),
                    AppErrorCodes.UNAUTHORIZED.getEnglishMessage(),
                    AppErrorCodes.UNAUTHORIZED.getCode(),
                    AppErrorCodes.UNAUTHORIZED.getStatus(),
                    request.getContextPath()
            );
        } else {
            restProblem = new RestProblem(
                    NovastoreConstants.DEFAULT_URL,
                    "خطای ناشناخته رخ داده است",
                    isProd() ? "unknown error occurred" : ex.getMessage(),
                    "500",
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    request.getContextPath()
            );
        }

        if (isProd())
            restProblem.setStackTrace(new StackTraceElement[0]);

        return new ResponseEntity<>(restProblem, restProblem.getStatus());
    }

    public void writeToResponse(HttpServletRequest request, HttpServletResponse response, Exception ex) throws IOException {
        RestProblem problem = buildProblemResponseEntity(request, ex).getBody();
        response.setStatus(problem.getStatusCode());
        response.setContentType("application/json; charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(problem));
    }


    private boolean isProd() {
        return Arrays.stream(environment.getActiveProfiles())
                .anyMatch(a -> a.contentEquals(NovastoreConstants.SPRING_PROFILE_STAGE) || a.contentEquals(NovastoreConstants.SPRING_PROFILE_PRODUCTION));
    }
}
