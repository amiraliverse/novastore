package com.app.novastore.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class NumberUtils {

    private NumberUtils() {
        // prevent instantiation
    }

    // Main entry points
    public static String convertToWords(BigDecimal number) {
        return convertToWords(number, false);
    }

    public static String convertToWords(BigDecimal number, boolean isOrdinal) {
        if (number.equals(BigDecimal.ZERO)) return "صفر";

        boolean negative = number.compareTo(BigDecimal.ZERO) < 0;
        number = number.abs();

        BigDecimal integerPart = new BigDecimal(number.toBigInteger());
        BigDecimal fractionPart = number.subtract(integerPart).setScale(number.scale(), RoundingMode.HALF_UP);

        String integerWords = convertIntegerToWords(integerPart);
        String fractionWords = convertFractionToWords(fractionPart);

        String result = integerWords;
        if (!result.isEmpty() && !fractionWords.isEmpty()) {
            result += " و " + fractionWords;
        } else if (result.isEmpty() && !fractionWords.isEmpty()) {
            result = fractionWords;
        }

        if (negative) {
            result = "منفی " + result;
        }

        if (isOrdinal) {
            result = appendOrdinalSuffix(result);
        }

        return result;
    }

    private static String convertIntegerToWords(BigDecimal number) {
        if (number.compareTo(BigDecimal.valueOf(999)) <= 0) {
            return convertSubThousandToWords(number);
        }

        String[] separated = String.format("%,d", number.longValue()).split(",");
        List<String> parts = new ArrayList<>();
        for (int i = 0; i < separated.length; i++) {
            BigDecimal chunk = new BigDecimal(separated[i]);
            String chunkWords = convertSubThousandToWords(chunk);
            String scaleName = getScaleName((separated.length - (i + 1)) * 3);

            if (!chunkWords.isEmpty()) {
                parts.add(chunkWords + (scaleName.isEmpty() ? "" : " " + scaleName));
            }
        }

        return String.join(" و ", parts).trim();
    }

    private static String convertFractionToWords(BigDecimal fraction) {
        if (fraction.compareTo(BigDecimal.ZERO) <= 0) return "";

        String fractionStr = String.valueOf(fraction).split("\\.")[1];
        BigDecimal fractionValue = new BigDecimal(fractionStr);

        String words = convertIntegerToWords(fractionValue);
        String unit = getFractionalUnit(fractionStr.length());
        return words + " " + unit;
    }

    private static String getFractionalUnit(int digits) {
        return switch (digits) {
            case 1 -> "دهم";
            case 2 -> "صدم";
            case 3 -> "هزارم";
            case 4 -> "ده هزارم";
            case 5 -> "صد هزارم";
            default -> "جزء";
        };
    }

    private static String convertSubThousandToWords(BigDecimal number) {
        if (number.compareTo(BigDecimal.ZERO) == 0)
            return "";

        if (number.compareTo(BigDecimal.valueOf(9)) <= 0)
            return getBasicNumberWord(number);

        if (number.compareTo(BigDecimal.valueOf(11)) >= 0 &&
            number.compareTo(BigDecimal.valueOf(19)) <= 0)
            return getBasicNumberWord(number);

        BigDecimal residual;
        if (number.compareTo(BigDecimal.valueOf(99)) <= 0) {
            residual = number.remainder(BigDecimal.TEN);
        } else {
            residual = number.remainder(BigDecimal.valueOf(100));
        }

        if (residual.compareTo(BigDecimal.ZERO) == 0) {
            return getBasicNumberWord(number);
        } else {
            return String.format("%s و %s",
                    getBasicNumberWord(number.subtract(residual)),
                    convertSubThousandToWords(residual));
        }
    }

    private static String getScaleName(int numberOfZeros) {
        if(numberOfZeros == 0)
            return "";
        return NumbersWordMap.getWord(BigDecimal.valueOf(Math.pow(10, numberOfZeros)));
    }

    private static String getBasicNumberWord(BigDecimal number) {
        return NumbersWordMap.getWord(number);
    }

    private static String appendNegativePrefix(String number) {
        return "منفی " + number;
    }

    private static String appendOrdinalSuffix(String number) {
        if (number.endsWith("ی")) {
            return number + " اُم";
        }

        if (number.endsWith("سه")) {
            return number.substring(0, number.length() - 2) + "سوم";
        }

        return number + "م";
    }


    private static final class NumbersWordMap {

        private static final Map<Long, String> WORDS = Map.ofEntries(
                Map.entry(1L, "یک"),
                Map.entry(2L, "دو"),
                Map.entry(3L, "سه"),
                Map.entry(4L, "چهار"),
                Map.entry(5L, "پنج"),
                Map.entry(6L, "شش"),
                Map.entry(7L, "هفت"),
                Map.entry(8L, "هشت"),
                Map.entry(9L, "نه"),
                Map.entry(10L, "ده"),
                Map.entry(11L, "یازده"),
                Map.entry(12L, "دوازده"),
                Map.entry(13L, "سیزده"),
                Map.entry(14L, "چهارده"),
                Map.entry(15L, "پانزده"),
                Map.entry(16L, "شانزده"),
                Map.entry(17L, "هفده"),
                Map.entry(18L, "هجده"),
                Map.entry(19L, "نوزده"),
                Map.entry(20L, "بیست"),
                Map.entry(30L, "سی"),
                Map.entry(40L, "چهل"),
                Map.entry(50L, "پنجاه"),
                Map.entry(60L, "شصت"),
                Map.entry(70L, "هفتاد"),
                Map.entry(80L, "هشتاد"),
                Map.entry(90L, "نود"),
                Map.entry(100L, "صد"),
                Map.entry(200L, "دویست"),
                Map.entry(300L, "سیصد"),
                Map.entry(400L, "چهارصد"),
                Map.entry(500L, "پانصد"),
                Map.entry(600L, "ششصد"),
                Map.entry(700L, "هفتصد"),
                Map.entry(800L, "هشتصد"),
                Map.entry(900L, "نهصد"),
                Map.entry(1_000L, "هزار"),
                Map.entry(1_000_000L, "میلیون"),
                Map.entry(1_000_000_000L, "میلیارد"),
                Map.entry(1_000_000_000_000L, "تریلیون"),
                Map.entry(1_000_000_000_000_000L, "کوآدریلیون")
        );

        private NumbersWordMap() {
            // prevent instantiation
        }

        public static String getWord(BigDecimal key) {
            return WORDS.get(key.longValue());
        }
    }

    public static void vain(String[] args) {
        BigDecimal num1 = BigDecimal.valueOf(1200L);
        BigDecimal num2 = BigDecimal.valueOf(34L);
        BigDecimal num3 = BigDecimal.valueOf(236000L);
        BigDecimal num4 = BigDecimal.valueOf(0.5D);
        BigDecimal num5 = BigDecimal.valueOf(1.34D);
        BigDecimal num6 = BigDecimal.valueOf(- 1.5D);
        BigDecimal num7 = BigDecimal.valueOf(- 0.512D);

        System.out.println(convertToWords(num1));
        System.out.println(convertToWords(num2));
        System.out.println(convertToWords(num3));
        System.out.println(convertToWords(num4));
        System.out.println(convertToWords(num5));
        System.out.println(convertToWords(num6));
        System.out.println(convertToWords(num7));
    }
}