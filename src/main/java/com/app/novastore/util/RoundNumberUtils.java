package com.app.novastore.util;

import java.math.BigDecimal;
import java.math.RoundingMode;

public final class RoundNumberUtils {

    public static BigDecimal roundUp(BigDecimal number, int decimalPlaces, int roundingScale) {
        return round(number, decimalPlaces, roundingScale, RoundingMode.HALF_UP);
    }

    public static BigDecimal roundDown(BigDecimal number, int decimalPlaces, int roundingScale) {
        return round(number, decimalPlaces, roundingScale, RoundingMode.DOWN);
    }

    public static BigDecimal round(BigDecimal value, int decimalPlaces, int roundingScale, RoundingMode mode) {
        if (value == null) return null;
        if (value.compareTo(BigDecimal.ZERO) == 0) return BigDecimal.ZERO;

        BigDecimal absValue = value.abs();
        int integerDigits = absValue.precision() - absValue.scale();

        BigDecimal rounded = value;

        if (roundingScale > 0 && integerDigits > roundingScale) {
            BigDecimal divisor = BigDecimal.TEN.pow(integerDigits - roundingScale);
            rounded = value.divide(divisor, 0, mode).multiply(divisor);
        }

        return rounded.setScale(decimalPlaces, mode);
    }

    public static void main(String[] args) {
        // Rial examples: roundingScale=3, decimalPlaces=0
        System.out.println(round(new BigDecimal("135450"), 0, 3, RoundingMode.UP)); // 136000
        System.out.println(round(new BigDecimal("1.39"), 0, 3, RoundingMode.UP));    // 2

        // Dollar examples: roundingScale=0, decimalPlaces=2
        System.out.println(round(new BigDecimal("1.395"), 2, 0, RoundingMode.UP));   // 1.40
        System.out.println(round(new BigDecimal("135450"), 2, 0, RoundingMode.UP));  // 135450.00
        System.out.println(round(new BigDecimal("0.056"), 2, 0, RoundingMode.UP));   // 0.06
    }
}
