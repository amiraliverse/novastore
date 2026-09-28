package com.app.novastore.exception;

import com.app.novastore.errors.AppErrorCodes;

public class RecordNotFoundException extends ApplicationException {
    public RecordNotFoundException(String logText) {
        super(AppErrorCodes.RECORD_NOT_FOUND, logText);
    }

    public RecordNotFoundException() {
        super(AppErrorCodes.RECORD_NOT_FOUND);
    }
}
