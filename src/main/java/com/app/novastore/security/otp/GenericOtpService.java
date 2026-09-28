package com.app.novastore.security.otp;

import com.app.novastore.util.StandardOtpCodeGenerator;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Setter(onMethod = @__({@Autowired}))
public abstract class GenericOtpService {

    protected GenericOtpRepo repository;

    protected StandardOtpCodeGenerator otpCodeGenerator;

    public Optional<GenericOtp> findById(String id) {
        return repository.findById(id);
    }

    public GenericOtp generate(String identifier) {
        return generate(identifier, otpCodeGenerator.getDefaultTimeToLive());
    }

    public GenericOtp generate(Instant expirationTime) {
        Instant now = Instant.now();
        if (!now.isBefore(expirationTime)) {
            throw new IllegalArgumentException("OTP expiration time must be in the future");
        }
        return generate(StringUtils.EMPTY, ChronoUnit.SECONDS.between(now, expirationTime));
    }

    public GenericOtp generate(Long timeToLive) {
        return generate(StringUtils.EMPTY, timeToLive);
    }

    public GenericOtp generate(String identifier, Long timeToLive) {
        return repository.save(GenericOtp.builder()
                .id(otpCodeGenerator.randomUUID())
                .identifier(identifier)
                .timeToLive(timeToLive)
                .code(otpCodeGenerator.randomCode())
                .accessKey(otpCodeGenerator.randomUUID())
                .build());
    }
}
