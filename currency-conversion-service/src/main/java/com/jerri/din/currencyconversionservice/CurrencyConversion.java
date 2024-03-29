package com.jerri.din.currencyconversionservice;

import lombok.Setter;
import lombok.Getter;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
public class CurrencyConversion {

    private Long id;
    private String currencyFrom;
    private String currencyTo;
    private BigDecimal conversionMultiple;
    private BigDecimal totalCalculatedAmount;
    private String environment;

}
