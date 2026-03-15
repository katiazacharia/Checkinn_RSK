package com.project.checkinn.ExchangeRate;

import com.project.checkinn.common.CurrencyCode;
import java.math.BigDecimal;


public class ExchangeRateService {

        BigDecimal getRate(CurrencyCode from, CurrencyCode to);

        BigDecimal convert(BigDecimal amount, CurrencyCode from, CurrencyCode to);
    }

