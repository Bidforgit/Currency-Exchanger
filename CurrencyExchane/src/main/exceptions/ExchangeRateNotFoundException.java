package main.exceptions;

import lombok.Getter;

@Getter
public class ExchangeRateNotFoundException extends RuntimeException {


    public ExchangeRateNotFoundException(String message) {
        super(message);
    }
}