package com.app.novastore.exception;

import com.app.novastore.errors.AppErrorCodes;

public class UserIsDisabledException extends ApplicationException {
    public UserIsDisabledException(String logText) {
        super(AppErrorCodes.USER_DISABLED, logText);
    }

    public UserIsDisabledException() {
        super(AppErrorCodes.USER_DISABLED);
    }
}
