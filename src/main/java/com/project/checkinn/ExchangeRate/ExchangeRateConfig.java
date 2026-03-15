package com.project.checkinn.ExchangeRate;

import com.project.checkinn.common.CurrencyCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ExchangeRateConfig {

        private final CurrencyCode baseCurrency;

        public ExchangeRateConfig(@Value("${app.currency.base}") CurrencyCode baseCurrency) {
            this.baseCurrency = baseCurrency;
        }

        public CurrencyCode getBaseCurrency() {
            return baseCurrency;
        }
    }

