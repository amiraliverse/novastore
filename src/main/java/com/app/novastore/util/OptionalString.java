package com.app.novastore.util;

import org.springframework.util.StringUtils;

import java.util.Optional;

public final class OptionalString {
    public static Optional<String> ofNullable(String value) {
        return StringUtils.hasText(value) ? Optional.of(value) : Optional.empty();
    }
}
