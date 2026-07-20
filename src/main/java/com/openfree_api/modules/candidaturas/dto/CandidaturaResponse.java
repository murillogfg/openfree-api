package com.openfree_api.modules.candidaturas.dto;

import com.openfree_api.modules.candidaturas.entity.StatusCandidatura;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class CandidaturaResponse {

    private Long id;

    private Long usuarioId;

    private String nome;

    private String email;

    private Long vagaId;

    private String vagaTitulo;

    private String mensagem;

    private BigDecimal valorProposto;

    private StatusCandidatura status;

    private Boolean empresaVisualizou;

    private LocalDateTime createdAt;

    

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
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

    public StatusCandidatura getStatus() {
        return status;
    }

    public void setStatus(StatusCandidatura status) {
        this.status = status;
    }

    public Boolean getEmpresaVisualizou() {
        return empresaVisualizou;
    }

    public void setEmpresaVisualizou(Boolean empresaVisualizou) {
        this.empresaVisualizou = empresaVisualizou;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}