package main.DTO;

import lombok.Getter;
import lombok.Setter;
import main.models.Currency;

import java.math.BigDecimal;

@Getter
@Setter
public class ExchangeRateWithCurrencies {
    private Long id;

    private Currency baseCurrency;

    private Currency targetCurrency;

    private BigDecimal rate;
}
