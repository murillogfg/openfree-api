package com.openfree_api.modules.candidaturas.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CreateCandidaturaRequest {

    @NotNull(message = "O usuário é obrigatório.")
    private Long usuarioId;

    private String mensagem;

    private BigDecimal valorProposto;

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

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