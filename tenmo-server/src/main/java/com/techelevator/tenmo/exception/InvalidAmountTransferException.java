package com.techelevator.tenmo.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidAmountTransferException extends Exception{

    public InvalidAmountTransferException(String message){
        super(message);
    }
}
