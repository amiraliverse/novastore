package com.app.novastore.exception;

import com.app.novastore.errors.AppErrorCodes;

public class ServiceTemporaryUnavailableException extends ApplicationException {
    public ServiceTemporaryUnavailableException(String logText) {
        super(AppErrorCodes.SERVICE_TEMPORARY_UNAVAILABLE, logText);
    }

    public ServiceTemporaryUnavailableException() {
        super(AppErrorCodes.SERVICE_TEMPORARY_UNAVAILABLE);
    }
}
