package com.kevinraupp.downloadandupload.downloadandupload.config.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FileNotFoundException extends RuntimeException{

    public FileNotFoundException(String e){
        super(e);
    }
    public FileNotFoundException(String e, Throwable cause){
        super(e, cause);
    }
}
