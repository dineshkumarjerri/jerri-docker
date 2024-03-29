package com.jerri.din.currencyconversionservice;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import org.springframework.context.annotation.Configuration;

@Configuration(proxyBeanMethods = false)
class RestTemplateConfiguration {

    @Bean
    RestTemplate restTemplate(RestTemplateBuilder builder) {
        return builder.build();
    }
}

@RestController
public class CurrencyConversionController {

    @Autowired
    private CurrencyExchangeProxy currencyExchangeProxy;

    @Autowired
    private RestTemplate restTemplate;

    @GetMapping("/currency-conversion/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversion(
            @PathVariable String from,
            @PathVariable String to,
            @PathVariable BigDecimal quantity
            ) {

        Map<String, String> uriVariables = new HashMap<>();
        uriVariables.put("from", from);
        uriVariables.put("to", to);
        ResponseEntity<CurrencyConversion> currencyConversionResponseEntity = restTemplate.getForEntity("http://localhost:8000/currency-exchange/from/USD/to/INR",
                CurrencyConversion.class, uriVariables);
        CurrencyConversion currencyConversion = currencyConversionResponseEntity.getBody();
        BigDecimal conversionValue = quantity.multiply(currencyConversion.getConversionMultiple());
        currencyConversion.setTotalCalculatedAmount(conversionValue);
        return currencyConversion;
    }

    @GetMapping("/currency-conversion-feign/from/{from}/to/{to}/quantity/{quantity}")
    public CurrencyConversion calculateCurrencyConversionFeign(
            @PathVariable String from,
            @PathVariable String to,
            @PathVariable BigDecimal quantity
    ) {

        CurrencyConversion currencyConversion = currencyExchangeProxy.getExchangeValue(from, to);
        BigDecimal conversionValue = quantity.multiply(currencyConversion.getConversionMultiple());
        currencyConversion.setTotalCalculatedAmount(conversionValue);
        currencyConversion.setEnvironment(currencyConversion.getEnvironment() + " feign");
        return currencyConversion;
    }
}
