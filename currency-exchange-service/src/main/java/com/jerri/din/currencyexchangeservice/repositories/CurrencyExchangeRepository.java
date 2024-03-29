package com.jerri.din.currencyexchangeservice.repositories;

import com.jerri.din.currencyexchangeservice.entities.CurrencyExchange;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurrencyExchangeRepository extends JpaRepository<CurrencyExchange, Long> {

    CurrencyExchange findByCurrencyFromAndCurrencyTo(String currencyFrom, String currencyTo);
}
