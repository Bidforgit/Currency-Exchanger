package main.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExchangeRates {

    private Long id;

    private Currency baseCurrencyId;

    private Currency targetCurrencyId;
}
