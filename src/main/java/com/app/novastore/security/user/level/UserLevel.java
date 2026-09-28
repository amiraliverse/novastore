package com.app.novastore.security.user.level;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Customer tier. Trade limits and pricing margins are both resolved against it, so
 * the stored value is the numeric rank: a future customer club can insert tiers
 * without rewriting existing rows.
 */
@Getter
@AllArgsConstructor
public enum UserLevel {

    BASIC(0, "پایه"),
    SILVER(1, "نقره‌ای"),
    GOLD(2, "طلایی"),
    VIP(3, "ویژه"),
    ;

    private final int value;
    private final String persianName;

    public static UserLevel valueOf(int value) {
        for (UserLevel userLevel : UserLevel.values()) {
            if (userLevel.value == value) {
                return userLevel;
            }
        }
        throw new IllegalArgumentException("Invalid value for UserLevel: " + value);
    }
}
