package main.exceptions;

import lombok.Getter;

@Getter
public class CurrencyAlreadyExistsException extends RuntimeException {
    private final String currencyCode;


    public CurrencyAlreadyExistsException( String currencyCode) {
//        super(message);
        this.currencyCode = currencyCode;
    }
}