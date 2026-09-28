package com.app.novastore.security.user.level.converter;

import com.app.novastore.security.user.level.UserLevel;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class UserLevelConverter implements AttributeConverter<UserLevel, Integer> {

    @Override
    public Integer convertToDatabaseColumn(UserLevel userLevel) {
        return userLevel == null ? null : userLevel.getValue();
    }

    @Override
    public UserLevel convertToEntityAttribute(Integer value) {
        if (value == null) {
            return null;
        }

        return UserLevel.valueOf(value);
    }
}
