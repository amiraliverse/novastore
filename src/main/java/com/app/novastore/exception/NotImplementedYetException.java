package com.app.novastore.exception;

import com.app.novastore.errors.AppErrorCodes;

public class NotImplementedYetException extends ApplicationException {
    public NotImplementedYetException(String logText) {
        super(AppErrorCodes.NOT_IMPLEMENTED_YET, logText);
    }

    public NotImplementedYetException() {
        super(AppErrorCodes.NOT_IMPLEMENTED_YET);
    }
}
