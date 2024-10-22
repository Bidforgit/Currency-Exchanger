package main.exceptions;

import lombok.Getter;

@Getter
public class CurrencyNotFoundException extends RuntimeException {
    private final String currencyCode;


    public CurrencyNotFoundException(String message, String currencyCode) {
        super(message);
        this.currencyCode = currencyCode;
    }
    public CurrencyNotFoundException(String currencyCode) {
        this.currencyCode = currencyCode;
    }
}