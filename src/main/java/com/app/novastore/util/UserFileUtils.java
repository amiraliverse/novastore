package com.app.novastore.util;

import com.app.novastore.security.SecurityUtils;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Arrays;

public final class UserFileUtils {

    public static String getFileName(String fileExtension) {
        String userId = SecurityUtils.getCurrentUserId();
        long timestamp = System.currentTimeMillis();
        byte[] hash;

        try {
            hash = Arrays.copyOfRange(
                    MessageDigest.getInstance("SHA-256").digest(userId.getBytes(StandardCharsets.UTF_8)),
                    0, 8
            );
        } catch (Exception e) {
            return userId + timestamp + fileExtension;
        }

        long userIdHash = ByteBuffer.wrap(hash).getLong();

        long combined = (userIdHash ^ timestamp);

        String shortCode = Base62Util.encode(ByteBuffer.allocate(Long.BYTES).putLong(combined).array());

        return shortCode + "." + fileExtension;
    }


    public static String getUserId(String fileName) {
        return Base62Util.decode(fileName.split("\\.")[0], StandardCharsets.UTF_8).split("-")[0];
    }

    public static String getFileExtension(String fileName) {
        int lastDotIndex = fileName.lastIndexOf(".");
        return (lastDotIndex != -1 && lastDotIndex < fileName.length() - 1)
                ? fileName.substring(lastDotIndex + 1)
                : "";
    }
}
