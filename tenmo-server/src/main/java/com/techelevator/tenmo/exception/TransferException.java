package com.techelevator.tenmo.exception;


public class TransferException extends RuntimeException {

    public TransferException(String message) {
        super(message);
    }
    public TransferException(Throwable throwable) {
        super(throwable);
    }
}
