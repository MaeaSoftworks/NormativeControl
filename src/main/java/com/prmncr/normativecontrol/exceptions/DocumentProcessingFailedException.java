package com.prmncr.normativecontrol.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DocumentProcessingFailedException extends RuntimeException {
    @Override
    public String getMessage() {
        return "An error occurred while processing document";
    }
}
