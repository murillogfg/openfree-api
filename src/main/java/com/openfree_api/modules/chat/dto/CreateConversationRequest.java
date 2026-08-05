package com.openfree_api.modules.chat.dto;

import jakarta.validation.constraints.NotNull;

public class CreateConversationRequest {

    @NotNull(message = "O ID da candidatura é obrigatório.")
    private Long candidaturaId;

    public Long getCandidaturaId() {
        return candidaturaId;
    }

    public void setCandidaturaId(Long candidaturaId) {
        this.candidaturaId = candidaturaId;
    }
}