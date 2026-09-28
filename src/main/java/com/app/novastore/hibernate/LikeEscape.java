package com.app.novastore.hibernate;

/**
 * Builds {@code LIKE} patterns out of user-supplied search terms.
 * <p>
 * Without this, a search term is concatenated straight into the pattern, so the wildcards
 * {@code %} and {@code _} arrive live: a filter of {@code "%"} matches every row in the
 * table, and {@code "_"} matches every single character. Callers pair the returned pattern
 * with {@link #ESCAPE_CHARACTER} on the {@code like} predicate.
 */
public final class LikeEscape {

    /** Backslash is not special to SQL {@code LIKE} until it is named as the escape. */
    public static final char ESCAPE_CHARACTER = '\\';

    private LikeEscape() {
    }

    /** {@code %term%}, with any wildcard inside {@code term} neutralised. */
    public static String contains(String term) {
        return "%" + escape(term) + "%";
    }

    /**
     * Escapes the escape character first - doing it later would double-escape the
     * backslashes this method itself just added.
     */
    public static String escape(String term) {
        return term
                .replace("\\", "\\\\")
                .replace("%", "\\%")
                .replace("_", "\\_");
    }
}
