package com.openfree_api.modules.profile.dto;

import jakarta.validation.constraints.Size;

public class UpdateCompanyProfileRequest {

    @Size(max = 120)
    private String nomeFantasia;

    @Size(max = 20)
    private String telefone;

    @Size(max = 2000)
    private String descricao;

    @Size(max = 100)
    private String cidade;

    @Size(min = 2, max = 2)
    private String estado;

    @Size(max = 255)
    private String site;

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
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

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }
}