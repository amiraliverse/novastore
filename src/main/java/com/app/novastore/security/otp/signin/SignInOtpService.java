package com.app.novastore.security.otp.signin;

import com.app.novastore.constants.NovastoreConstants;
import com.app.novastore.security.otp.GenericOtp;
import com.app.novastore.security.otp.GenericOtpService;
import lombok.AllArgsConstructor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@AllArgsConstructor
@Service(SignInOtpService.BEAN_NAME)
public class SignInOtpService extends GenericOtpService {
    public static final String BEAN_NAME = "signInOtpService";

    private final Environment environment;

    @Override
    public GenericOtp generate(String identifier, Long timeToLive) {
        String code;
        List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
        if (activeProfiles.contains(NovastoreConstants.SPRING_PROFILE_TEST)) {
            code = "1111";
        } else if (activeProfiles.contains(NovastoreConstants.SPRING_PROFILE_EASY_OTP)) {
            code = otpCodeGenerator.randomEasyCode();
        } else {
            code = otpCodeGenerator.randomCode();
        }

        return repository.save(GenericOtp.builder()
                .id(identifier)
                .identifier(identifier)
                .timeToLive(timeToLive)
                .code(code)
                .accessKey(otpCodeGenerator.randomUUID())
                .build());
    }
}
