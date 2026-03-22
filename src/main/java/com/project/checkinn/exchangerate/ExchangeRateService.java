package com.project.checkinn.exchangerate;

import com.project.checkinn.common.CurrencyCode;
import java.math.BigDecimal;


public interface ExchangeRateService {

        BigDecimal getRate(CurrencyCode from, CurrencyCode to);

        BigDecimal convert(BigDecimal amount, CurrencyCode from, CurrencyCode to);
    }

