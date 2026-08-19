package com.openfree_api.modules.contracts.dto;

import com.openfree_api.modules.contracts.entity.ContractStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class ContractResponse {

    private Long id;

    private Long candidaturaId;

    private Long vagaId;

    private String vagaTitulo;

    private Long empresaId;

    private String empresaNome;

    private Long freelancerId;

    private String freelancerNome;

    private BigDecimal valor;

    private ContractStatus status;

    private Boolean empresaConfirmouConclusao;

    private Boolean freelancerConfirmouConclusao;

    private LocalDateTime iniciadoAt;

    private LocalDateTime concluidoAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    public ContractResponse() {
    }

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

    public Long getFreelancerId() {
        return freelancerId;
    }

    public void setFreelancerId(Long freelancerId) {
        this.freelancerId = freelancerId;
    }

    public String getFreelancerNome() {
        return freelancerNome;
    }

    public void setFreelancerNome(String freelancerNome) {
        this.freelancerNome = freelancerNome;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public ContractStatus getStatus() {
        return status;
    }

    public void setStatus(ContractStatus status) {
        this.status = status;
    }

    public Boolean getEmpresaConfirmouConclusao() {
        return empresaConfirmouConclusao;
    }

    public void setEmpresaConfirmouConclusao(
            Boolean empresaConfirmouConclusao
    ) {
        this.empresaConfirmouConclusao =
                empresaConfirmouConclusao;
    }

    public Boolean getFreelancerConfirmouConclusao() {
        return freelancerConfirmouConclusao;
    }

    public void setFreelancerConfirmouConclusao(
            Boolean freelancerConfirmouConclusao
    ) {
        this.freelancerConfirmouConclusao =
                freelancerConfirmouConclusao;
    }

    public LocalDateTime getIniciadoAt() {
        return iniciadoAt;
    }

    public void setIniciadoAt(
            LocalDateTime iniciadoAt
    ) {
        this.iniciadoAt = iniciadoAt;
    }

    public LocalDateTime getConcluidoAt() {
        return concluidoAt;
    }

    public void setConcluidoAt(
            LocalDateTime concluidoAt
    ) {
        this.concluidoAt = concluidoAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(
            LocalDateTime createdAt
    ) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(
            LocalDateTime updatedAt
    ) {
        this.updatedAt = updatedAt;
    }
}