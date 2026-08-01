package com.openfree_api.modules.candidaturas.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public class CreateCandidaturaRequest {

    @Size(max = 1000)
    private String mensagem;

    @DecimalMin(
            value = "0.01",
            message = "O valor proposto deve ser maior que zero."
    )
    private BigDecimal valorProposto;

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public BigDecimal getValorProposto() {
        return valorProposto;
    }

    public void setValorProposto(BigDecimal valorProposto) {
        this.valorProposto = valorProposto;
    }
}