package main.exceptions;

import lombok.Getter;

@Getter
public class ExchangeRateAlreadyExistsException extends RuntimeException {


    public ExchangeRateAlreadyExistsException(String message) {
        super(message);
    }
}