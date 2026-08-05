package com.openfree_api.modules.chat.dto;

import com.openfree_api.modules.chat.entity.MessageSenderType;

import java.time.LocalDateTime;

public class MessageResponse {

    private Long id;
    private Long remetenteId;
    private String remetenteNome;
    private MessageSenderType tipoRemetente;
    private String conteudo;
    private Boolean lida;
    private LocalDateTime createdAt;
    private LocalDateTime readAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getRemetenteId() {
        return remetenteId;
    }

    public void setRemetenteId(Long remetenteId) {
        this.remetenteId = remetenteId;
    }

    public String getRemetenteNome() {
        return remetenteNome;
    }

    public void setRemetenteNome(String remetenteNome) {
        this.remetenteNome = remetenteNome;
    }

    public MessageSenderType getTipoRemetente() {
        return tipoRemetente;
    }

    public void setTipoRemetente(
            MessageSenderType tipoRemetente
    ) {
        this.tipoRemetente = tipoRemetente;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(String conteudo) {
        this.conteudo = conteudo;
    }

    public Boolean getLida() {
        return lida;
    }

    public void setLida(Boolean lida) {
        this.lida = lida;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }

    public void setReadAt(LocalDateTime readAt) {
        this.readAt = readAt;
    }
}