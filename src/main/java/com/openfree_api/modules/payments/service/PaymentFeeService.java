package com.openfree_api.modules.payments.service;

import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
public class PaymentFeeService {

    private static final BigDecimal TAXA =
            new BigDecimal("0.00");

    public BigDecimal calcularTaxa(
            BigDecimal valor
    ) {

        return valor
                .multiply(TAXA)
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );
    }

    public BigDecimal calcularLiquido(
            BigDecimal valor
    ) {

        return valor
                .subtract(
                        calcularTaxa(valor)
                )
                .setScale(
                        2,
                        RoundingMode.HALF_UP
                );
    }
}