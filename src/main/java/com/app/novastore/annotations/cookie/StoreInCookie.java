package com.app.novastore.annotations.cookie;


import org.springframework.core.annotation.AliasFor;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface StoreInCookie {
    @AliasFor("value")
    String key() default "";

    @AliasFor("key")
    String value() default "";

    int maxAge() default 0;

    boolean httpOnly() default true;

    boolean secure() default true;
}
