package com.app.novastore.hibernate.converter;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Converts a {@link List} of {@link String} to a delimited {@link String} for database storage,
 * and vice versa. Useful for storing lists as comma-separated values (CSV) in database columns.
 *
 * <p>This converter supports any delimiter, with a default delimiter of a comma (",").</p>
 *
 * <p>Usage Example:</p>
 * <pre>
 * &#64;Convert(converter = DelimitedStringConverter.class)
 * private List&lt;String&gt; tags;
 * </pre>
 */
@Converter(autoApply = true)
public class DelimitedLongConverter implements AttributeConverter<List<Long>, String> {

    private final String delimiter;

    /**
     * Constructs a {@code DelimitedStringConverter} with the specified delimiter.
     *
     * @param delimiter the delimiter used to separate list elements in the database column
     */
    protected DelimitedLongConverter(String delimiter) {
        this.delimiter = delimiter;
    }

    /**
     * Default constructor that initializes the converter with a comma (",") as the delimiter.
     */
    public DelimitedLongConverter() {
        this(",");
    }

    /**
     * Converts a list of strings into a single delimited string for database storage.
     *
     * @param attribute the list of strings to be converted
     * @return a single string with elements separated by the delimiter, or {@code null} if the input is {@code null} or empty
     */
    @Override
    public String convertToDatabaseColumn(List<Long> attribute) {
        return (attribute == null || attribute.isEmpty())
                ? null
                : attribute.stream().map(String::valueOf).collect(Collectors.joining(delimiter));
    }

    /**
     * Converts a delimited string from the database into a list of strings.
     *
     * @param dbData the delimited string from the database
     * @return a list of strings parsed from the input, or an empty list if the input is {@code null} or blank
     */
    @Override
    public List<Long> convertToEntityAttribute(String dbData) {
        if (!StringUtils.hasText(dbData))
            return new ArrayList<>();

        return Arrays.stream(dbData.split(delimiter))
                .map(String::trim)
                .map(Long::parseLong)
                .toList();
    }
}

