package com.app.novastore.exception;

import com.app.novastore.errors.AppErrorCodes;

public class OtpRequestCountExceededException extends ApplicationException {
    public OtpRequestCountExceededException() {
        super(AppErrorCodes.OTP_REQUEST_COUNT_EXCEEDED);
    }
}
