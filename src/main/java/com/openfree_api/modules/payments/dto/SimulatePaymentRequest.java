package com.openfree_api.modules.payments.dto;

import com.openfree_api.modules.payments.entity.PaymentMethod;

import jakarta.validation.constraints.NotNull;

public class SimulatePaymentRequest {

    @NotNull(
            message = "O método de pagamento é obrigatório."
    )
    private PaymentMethod metodo;

    public PaymentMethod getMetodo() {
        return metodo;
    }

    public void setMetodo(
            PaymentMethod metodo
    ) {
        this.metodo = metodo;
    }
}