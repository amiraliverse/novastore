package com.app.novastore.hibernate.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import lombok.AllArgsConstructor;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.json.JsonMapper;

import java.util.HashMap;
import java.util.Map;

@AllArgsConstructor
@Converter(autoApply = true)
public class JsonMapConverter implements AttributeConverter<Map<String, Object>, String> {
    private final JsonMapper mapper;

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        try {
            return attribute == null ? null : mapper.writeValueAsString(attribute);
        } catch (JacksonException e) {
            throw new IllegalStateException("Error converting map to JSON", e);
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        try {
            return dbData == null ? new HashMap<>() : mapper.readValue(dbData, new TypeReference<>() {});
        } catch (JacksonException e) {
            throw new IllegalStateException("Error reading JSON from DB", e);
        }
    }
}
