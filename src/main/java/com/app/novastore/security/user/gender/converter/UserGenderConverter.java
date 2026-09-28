package com.app.novastore.security.user.gender.converter;

import com.app.novastore.security.user.gender.UserGender;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class UserGenderConverter implements AttributeConverter<UserGender, Integer> {
    @Override
    public Integer convertToDatabaseColumn(UserGender userGender) {
        return userGender == null ? null : userGender.getValue();
    }

    @Override
    public UserGender convertToEntityAttribute(Integer value) {
        if (value == null) {
            return null;
        }

        return UserGender.valueOf(value);
    }
}
