package com.app.novastore.annotations.baseinfo;

import java.lang.annotation.*;

@Documented
@Target({ElementType.FIELD, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface BaseInfoCode {
    String value();
}
