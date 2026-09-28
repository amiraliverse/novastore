package com.app.novastore.util;

/**
 * Builds safe SQL {@code LIKE} patterns from raw user input. The wildcard
 * characters {@code %} and {@code _} (and the escape character itself) are
 * escaped so they match literally instead of acting as wildcards. Callers pass
 * {@link #ESCAPE_CHARACTER} as the escape argument of
 * {@code CriteriaBuilder#like(expression, pattern, escapeChar)}.
 */
public final class LikeEscape {

    /** Escape character declared to the database in the {@code LIKE ... ESCAPE} clause. */
    public static final char ESCAPE_CHARACTER = '\\';

    private LikeEscape() {
    }

    /** {@code %value%} with the wildcards inside {@code value} escaped. */
    public static String contains(String value) {
        return "%" + escape(value) + "%";
    }

    private static String escape(String value) {
        if (value == null) {
            return "";
        }
        return value
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
