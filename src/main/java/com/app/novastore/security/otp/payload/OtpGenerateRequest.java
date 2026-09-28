package com.app.novastore.security.otp.payload;

import com.app.novastore.annotations.otp.OtpId;
import com.app.novastore.errors.ConstantErrorMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OtpGenerateRequest {

    @OtpId
    @NotBlank
    @Pattern(regexp = "^(09)\\d{9}$", message = ConstantErrorMessage.MOBILE_PHONE_NUMBER_VALIDATION_MESSAGE)
    private String mobile;

    private String accessKey;

    private String captcha;

}
