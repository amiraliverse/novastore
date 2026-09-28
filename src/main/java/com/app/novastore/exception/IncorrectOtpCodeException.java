package com.app.novastore.exception;

import com.app.novastore.errors.AppErrorCodes;

public class IncorrectOtpCodeException extends ApplicationException {
    public IncorrectOtpCodeException() {
        super(AppErrorCodes.INCORRECT_OTP_CODE);
    }
}
