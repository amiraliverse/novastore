package com.app.novastore.util;

import com.app.novastore.config.NovastoreProperties;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Component
public class StandardOtpCodeGenerator {

    private static final SecureRandom random = new SecureRandom();

    private final NovastoreProperties.Security.Otp otpProperties;

    public StandardOtpCodeGenerator(NovastoreProperties properties) {
        this.otpProperties = properties.getSecurity().getOtp();
    }

    public String randomCode() {
        return org.apache.commons.lang3.StringUtils.right(
                TSIDUtils.randomNumber().toString(),
                otpProperties.getLength()
        );
    }

    public String randomEasyCode() {
        String[] patterns = otpProperties.getPatterns();
        String pattern = patterns[random.nextInt(patterns.length)];

        Map<Character, Integer> digitMap = new HashMap<>();
        for (char ch : pattern.toCharArray()) {
            digitMap.putIfAbsent(ch, random.nextInt(10));
        }

        StringBuilder otp = new StringBuilder();
        for (char ch : pattern.toCharArray()) {
            otp.append(digitMap.get(ch));
        }

        return otp.toString();
    }

    public Instant getDefaultExpirationTime() {
        return LocalDateTime.now()
                .atZone(ZoneId.systemDefault())
                .plusSeconds(otpProperties.getExpirationTimeInSeconds())
                .toInstant();
    }

    public Long getDefaultTimeToLive() {
        return otpProperties.getExpirationTimeInSeconds();
    }

    public String randomUUID() {
        return UUID.randomUUID().toString();
    }
}
