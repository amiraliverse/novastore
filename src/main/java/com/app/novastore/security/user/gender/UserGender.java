package com.app.novastore.security.user.gender;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum UserGender {
    MALE(0),
    FEMALE(1),
    UNKNOWN(2),
    ;
    private final int value;

    public static UserGender valueOf(int value) {
        for (UserGender userGender : UserGender.values()) {
            if (userGender.value == value) {
                return userGender;
            }
        }
        throw new IllegalArgumentException("Invalid value for UserGender: " + value);
    }
}
