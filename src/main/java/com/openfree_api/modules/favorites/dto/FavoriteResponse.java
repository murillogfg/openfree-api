package com.openfree_api.modules.favorites.dto;

import com.openfree_api.modules.jobs.entity.StatusVaga;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class FavoriteResponse {

    private Long id;
    private Long vagaId;
    private String titulo;
    private String empresaNome;
    private String cidade;
    private String estado;
    private BigDecimal valor;
    private LocalDate dataServico;
    private StatusVaga status;
    private LocalDateTime favoritadoEm;

    public FavoriteResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getEmpresaNome() {
        return empresaNome;
    }

    public void setEmpresaNome(String empresaNome) {
        this.empresaNome = empresaNome;
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

    public StatusVaga getStatus() {
        return status;
    }

    public void setStatus(StatusVaga status) {
        this.status = status;
    }

    public LocalDateTime getFavoritadoEm() {
        return favoritadoEm;
    }

    public void setFavoritadoEm(LocalDateTime favoritadoEm) {
        this.favoritadoEm = favoritadoEm;
    }
}
