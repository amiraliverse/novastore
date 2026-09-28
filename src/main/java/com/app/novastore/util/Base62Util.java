package com.app.novastore.util;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public final class Base62Util {
    private static final String BASE62_CHARS = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = 62;

    /**
     * Encodes a byte array into a Base62 string.
     */
    public static String encode(byte[] data) {
        return encode(new BigInteger(1, data));
    }

    /**
     * Encodes a number into a Base62 string.
     */
    public static String encode(BigInteger number) {
        if (number.compareTo(BigInteger.ZERO) == 0) {
            return "0";
        }

        StringBuilder sb = new StringBuilder();
        while (number.compareTo(BigInteger.ZERO) > 0) {
            BigInteger[] divmod = number.divideAndRemainder(BigInteger.valueOf(BASE));
            number = divmod[0];
            sb.append(BASE62_CHARS.charAt(divmod[1].intValue()));
        }
        return sb.reverse().toString();
    }

    /**
     * Decodes a Base62 string into a byte array.
     */
    public static byte[] decodeToBytes(String base62) {
        BigInteger number = decodeToBigInteger(base62);
        return number.toByteArray();
    }

    /**
     * Decodes a Base62 string into a BigInteger.
     */
    public static BigInteger decodeToBigInteger(String base62) {
        BigInteger number = BigInteger.ZERO;
        for (char c : base62.toCharArray()) {
            number = number.multiply(BigInteger.valueOf(BASE)).add(BigInteger.valueOf(BASE62_CHARS.indexOf(c)));
        }
        return number;
    }

    /**
     * Encodes a string into Base62.
     */
    public static String encode(String input) {
        return encode(input.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Decodes a Base62 string back to the original string.
     */
    public static String decode(String base62, Charset charset) {
        return new String(decodeToBytes(base62), charset);
    }
}
