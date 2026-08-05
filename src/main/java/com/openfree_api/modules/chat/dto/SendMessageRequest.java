package com.openfree_api.modules.chat.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class SendMessageRequest {

    @NotBlank(message = "O conteúdo da mensagem é obrigatório.")
    @Size(
            max = 2000,
            message = "A mensagem deve possuir no máximo 2000 caracteres."
    )
    private String conteudo;

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }
}