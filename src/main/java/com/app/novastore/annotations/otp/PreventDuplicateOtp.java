package com.app.novastore.annotations.otp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * target parameter should have a field annotated with {@link OtpId} to do query by this field value in redis
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface PreventDuplicateOtp {
    String value() default "";
}
