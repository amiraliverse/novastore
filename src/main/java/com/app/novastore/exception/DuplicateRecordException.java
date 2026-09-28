package com.app.novastore.exception;


import com.app.novastore.errors.AppErrorCodes;

public class DuplicateRecordException extends ApplicationException {
    public DuplicateRecordException() {
        super(AppErrorCodes.DUPLICATE_RECORD);
    }

    public DuplicateRecordException(String logText) {
        super(AppErrorCodes.DUPLICATE_RECORD, logText);
    }
}
