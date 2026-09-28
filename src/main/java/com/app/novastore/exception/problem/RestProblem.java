package com.app.novastore.exception.problem;

import com.app.novastore.constants.NovastoreConstants;
import com.app.novastore.errors.AppErrorCodes;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.net.URI;
import java.util.Date;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_EMPTY)
@JsonPropertyOrder({"type", "persianMessage", "englishMessage", "errorCode", "status", "statusCode", "timestamp", "path", "stackTrace"})
public class RestProblem extends RuntimeException {
    private URI type;
    private String persianMessage;
    private String englishMessage;
    private String errorCode;
    private HttpStatus status;
    private int statusCode;
    private Date timestamp;
    private String path;

    public RestProblem(AppErrorCodes appErrorCodes) {
        this.type = NovastoreConstants.DEFAULT_URL;
        this.persianMessage = appErrorCodes.getPersianMessage();
        this.englishMessage = appErrorCodes.getEnglishMessage();
        this.errorCode = appErrorCodes.getCode();
        this.status = appErrorCodes.getStatus();
        this.statusCode = appErrorCodes.getStatus().value();
        this.timestamp = new Date();
    }

    public RestProblem(URI type, String persianMessage, String englishMessage, String errorCode, HttpStatus status) {
        this.type = type;
        this.persianMessage = persianMessage;
        this.englishMessage = englishMessage;
        this.errorCode = errorCode;
        this.status = status;
        this.statusCode = status.value();
        this.timestamp = new Date();
    }

    public RestProblem(URI type, String persianMessage, String englishMessage, String errorCode, HttpStatus status, String path) {
        this.type = type;
        this.persianMessage = persianMessage;
        this.englishMessage = englishMessage;
        this.errorCode = errorCode;
        this.status = status;
        this.statusCode = status.value();
        this.timestamp = new Date();
        this.path = path;
    }
}
