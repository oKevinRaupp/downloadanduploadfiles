package com.kevinraupp.downloadandupload.downloadandupload.config.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INSUFFICIENT_STORAGE)
public class FileStorageException extends RuntimeException{

    public FileStorageException(String e){
        super(e);
    }
    public FileStorageException(String e, Throwable cause){
        super(e, cause);
    }
}
