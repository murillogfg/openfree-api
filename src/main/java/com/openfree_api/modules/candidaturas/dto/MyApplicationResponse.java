package com.openfree_api.modules.candidaturas.dto;

import com.openfree_api.modules.candidaturas.entity.StatusCandidatura;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class MyApplicationResponse {

    private Long candidaturaId;

    private Long vagaId;

    private String titulo;

    private String empresa;

    private String cidade;

    private String estado;

    private BigDecimal valor;

    private LocalDate dataServico;

    private StatusCandidatura status;

    private Boolean empresaVisualizou;

    private LocalDateTime dataCandidatura;

    public MyApplicationResponse() {
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

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public LocalDate getDataServico() {
        return dataServico;
    }

    public void setDataServico(LocalDate dataServico) {
        this.dataServico = dataServico;
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

    public LocalDateTime getDataCandidatura() {
        return dataCandidatura;
    }

    public void setDataCandidatura(LocalDateTime dataCandidatura) {
        this.dataCandidatura = dataCandidatura;
    }
}