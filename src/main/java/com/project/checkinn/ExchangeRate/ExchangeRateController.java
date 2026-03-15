package com.project.checkinn.ExchangeRate;



import com.project.checkinn.common.CurrencyCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.math.BigDecimal;
import java.util.Map;

@RestController
@RequestMapping("/exchange-rates")
public class ExchangeRateController {



        private final ExchangeRateService exchangeRateService;

        public ExchangeRateController(ExchangeRateService exchangeRateService) {
            this.exchangeRateService = exchangeRateService;
        }

        @GetMapping("/rate")
        public Map<String, Object> getRate(
                @RequestParam CurrencyCode from,
                @RequestParam CurrencyCode to
        ) {
            BigDecimal rate = exchangeRateService.getRate(from, to);

            return Map.of(
                    "from", from,
                    "to", to,
                    "rate", rate
            );
        }

        @GetMapping("/convert")
        public Map<String, Object> convert(
                @RequestParam BigDecimal amount,
                @RequestParam CurrencyCode from,
                @RequestParam CurrencyCode to
        ) {
            BigDecimal converted = exchangeRateService.convert(amount, from, to);

            return Map.of(
                    "amount", amount,
                    "from", from,
                    "to", to,
                    "convertedAmount", converted
            );
        }
    }

