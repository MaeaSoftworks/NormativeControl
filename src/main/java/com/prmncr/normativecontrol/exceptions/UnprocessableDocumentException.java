package com.prmncr.normativecontrol.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
public class UnprocessableDocumentException extends RuntimeException {
    @Override
    public String getMessage() {
        return "Can not process this document";
    }
}
