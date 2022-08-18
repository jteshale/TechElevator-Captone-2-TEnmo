package com.techelevator.tenmo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidTransferAccountException extends Exception{

    public InvalidTransferAccountException(String message){
        super(message);
    }
}
