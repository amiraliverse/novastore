package com.app.novastore.exception;

import com.app.novastore.constants.NovastoreConstants;
import com.app.novastore.errors.AppErrorCodes;
import com.app.novastore.exception.problem.RestProblem;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;

@Slf4j
public class ApplicationException extends RestProblem {

    public ApplicationException(AppErrorCodes errorCode) {
        super(errorCode);
    }

    public ApplicationException(AppErrorCodes errorCode, String logText) {
        this(errorCode);
        log.error(logText);
    }

    public ApplicationException(String englishMessage, String persianMessage, String errorCode) {
        super(
                NovastoreConstants.DEFAULT_URL,
                persianMessage,
                englishMessage,
                errorCode,
                HttpStatus.BAD_REQUEST
        );
    }

    public ApplicationException(String englishMessage, String persianMessage, String errorCode, String logText) {
        super(
                NovastoreConstants.DEFAULT_URL,
                persianMessage,
                englishMessage,
                errorCode,
                HttpStatus.BAD_REQUEST
        );
        log.error(logText);
    }
}
