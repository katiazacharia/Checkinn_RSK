package com.project.checkinn.exchangerate;


import com.project.checkinn.common.CurrencyCode;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {


        private static final String BASE_URL = "https://api.frankfurter.dev/v1";

        private final RestClient restClient;

        public ExchangeRateServiceImpl() {
            this.restClient = RestClient.builder()
                    .baseUrl(BASE_URL)
                    .build();
        }

        @Override
        public BigDecimal getRate(CurrencyCode from, CurrencyCode to) {
            if (from == null || to == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "from and to currencies are required");
            }

            if (from == to) {
                return BigDecimal.ONE;
            }

            ExchangeRateApiResponse response = restClient.get()
                    .uri(uriBuilder -> uriBuilder
                            .path("/latest")
                            .queryParam("base", from.name())
                            .queryParam("symbols", to.name())
                            .build())
                    .retrieve()
                    .body(ExchangeRateApiResponse.class);

            if (response == null || response.getRates() == null || !response.getRates().containsKey(to.name())) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Could not fetch exchange rate");
            }

            BigDecimal rate = response.getRates().get(to.name());

            if (rate == null) {
                throw new ResponseStatusException(HttpStatus.BAD_GATEWAY, "Exchange rate not found");
            }

            return rate;
        }

        @Override
        public BigDecimal convert(BigDecimal amount, CurrencyCode from, CurrencyCode to) {
            if (amount == null) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount is required");
            }

            if (amount.compareTo(BigDecimal.ZERO) < 0) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "amount must be >= 0");
            }

            if (from == to) {
                return amount.setScale(2, RoundingMode.HALF_UP);
            }

            BigDecimal rate = getRate(from, to);

            return amount.multiply(rate).setScale(2, RoundingMode.HALF_UP);
        }
    }


