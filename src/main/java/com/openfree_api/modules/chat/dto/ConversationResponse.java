package com.openfree_api.modules.chat.dto;

import com.openfree_api.modules.chat.entity.ConversationStatus;

import java.time.LocalDateTime;

public class ConversationResponse {

    private Long id;
    private Long candidaturaId;
    private Long vagaId;
    private String vagaTitulo;
    private Long empresaId;
    private String empresaNome;
    private Long usuarioId;
    private String usuarioNome;
    private ConversationStatus status;
    private long mensagensNaoLidas;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCandidaturaId() {
        return candidaturaId;
    }

    public void setCandidaturaId(Long candidaturaId) {
        this.candidaturaId = candidaturaId;
    }

    public Long getVagaId() {
        return vagaId;
    }

    public void setVagaId(Long vagaId) {
        this.vagaId = vagaId;
    }

    public String getVagaTitulo() {
        return vagaTitulo;
    }

    public void setVagaTitulo(String vagaTitulo) {
        this.vagaTitulo = vagaTitulo;
    }

    public Long getEmpresaId() {
        return empresaId;
    }

    public void setEmpresaId(Long empresaId) {
        this.empresaId = empresaId;
    }

    public String getEmpresaNome() {
        return empresaNome;
    }

    public void setEmpresaNome(String empresaNome) {
        this.empresaNome = empresaNome;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getUsuarioNome() {
        return usuarioNome;
    }

    public void setUsuarioNome(String usuarioNome) {
        this.usuarioNome = usuarioNome;
    }

    public ConversationStatus getStatus() {
        return status;
    }

    public void setStatus(
            ConversationStatus status
    ) {
        this.status = status;
    }

    public long getMensagensNaoLidas() {
        return mensagensNaoLidas;
    }

    public void setMensagensNaoLidas(
            long mensagensNaoLidas
    ) {
        this.mensagensNaoLidas = mensagensNaoLidas;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}