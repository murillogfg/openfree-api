package com.openfree_api.modules.jobs.dto;

import com.openfree_api.modules.jobs.entity.StatusVaga;

public class JobFilterRequest {

    private String titulo;
    private String cidade;
    private String estado;
    private StatusVaga status;

    public JobFilterRequest() {
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
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

    public StatusVaga getStatus() {
        return status;
    }

    public void setStatus(StatusVaga status) {
        this.status = status;
    }
}