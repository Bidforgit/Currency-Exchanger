package main.DTO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;
import main.models.Currency;

import java.math.BigDecimal;

@Getter
@Setter
public class ExchangeRateWithCurrencies {

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Long id;

    private Currency baseCurrency;

    private Currency targetCurrency;

    private BigDecimal rate;

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private  BigDecimal convertedAmount;
}
