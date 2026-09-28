package com.app.novastore.util;

import com.github.mfathi91.time.PersianDate;

import java.time.LocalDate;

/**
 * Jalali helpers. Everything the trading core stores stays Gregorian; the Jalali form
 * is used only where the business defines the boundary in that calendar: order codes
 * are numbered per Jalali year and monthly trade limits reset on the first of the
 * Jalali month.
 */
public final class JalaliDateUtils {

    private JalaliDateUtils() {
    }

    public static PersianDate of(LocalDate gregorianDate) {
        return PersianDate.fromGregorian(gregorianDate);
    }

    public static int year(LocalDate gregorianDate) {
        return of(gregorianDate).getYear();
    }

    /** {@code 1405/05/21} */
    public static String format(LocalDate gregorianDate) {
        PersianDate date = of(gregorianDate);
        return "%04d/%02d/%02d".formatted(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    /** {@code 14050521}: the Jalali date with no separators, for compact codes. */
    public static String basicDate(LocalDate gregorianDate) {
        PersianDate date = of(gregorianDate);
        return "%04d%02d%02d".formatted(date.getYear(), date.getMonthValue(), date.getDayOfMonth());
    }

    /** Day of the Jalali month (1..31) for the given Gregorian date. */
    public static int dayOfJalaliMonth(LocalDate gregorianDate) {
        return of(gregorianDate).getDayOfMonth();
    }

    /** Gregorian date of the first day of the Jalali month the given date falls in. */
    public static LocalDate firstDayOfJalaliMonth(LocalDate gregorianDate) {
        PersianDate date = of(gregorianDate);
        return PersianDate.of(date.getYear(), date.getMonthValue(), 1).toGregorian();
    }
}
