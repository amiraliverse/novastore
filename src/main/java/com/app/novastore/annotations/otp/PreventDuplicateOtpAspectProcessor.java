package com.app.novastore.annotations.otp;

import com.app.novastore.exception.OtpRequestCountExceededException;
import com.app.novastore.security.otp.GenericOtpService;
import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import java.util.Arrays;

@Aspect
@Component
@AllArgsConstructor
public class PreventDuplicateOtpAspectProcessor {

    private final GenericOtpService otpService;

    @Before("@annotation(com.app.novastore.annotations.otp.PreventDuplicateOtp)")
    public void preventDuplicateOtp(JoinPoint joinPoint) {
        Arrays.stream(joinPoint.getArgs())
                .forEach(arg ->
                        Arrays.stream(arg.getClass().getDeclaredFields())
                                .filter(field -> field.isAnnotationPresent(OtpId.class))
                                .findFirst()
                                .ifPresent(field -> {
                                    field.setAccessible(true);
                                    try {
                                        Object fieldValue = field.get(arg);
                                        if (fieldValue != null) {
                                            otpService.findById(fieldValue.toString())
                                                    .ifPresent(otp -> {
                                                        throw new OtpRequestCountExceededException();
                                                    });
                                        }
                                    } catch (IllegalAccessException e) {
                                        throw new RuntimeException("Failed to access field value", e);
                                    }
                                }));
    }

}
