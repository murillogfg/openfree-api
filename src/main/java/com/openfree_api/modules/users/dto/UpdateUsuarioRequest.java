package com.openfree_api.modules.users.dto;

import jakarta.validation.constraints.Size;

public class UpdateUsuarioRequest {

    @Size(max = 120)
    private String nome;

    @Size(max = 20)
    private String telefone;

    @Size(max = 120)
    private String tituloProfissional;

    @Size(max = 2000)
    private String biografia;

    @Size(max = 100)
    private String cidade;

    @Size(max = 2)
    private String estado;

    @Size(max = 2000)
    private String habilidades;

    @Size(max = 500)
    private String portfolioUrl;

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getTituloProfissional() {
        return tituloProfissional;
    }

    public void setTituloProfissional(
            String tituloProfissional
    ) {
        this.tituloProfissional =
                tituloProfissional;
    }

    public String getBiografia() {
        return biografia;
    }

    public void setBiografia(String biografia) {
        this.biografia = biografia;
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

    public String getHabilidades() {
        return habilidades;
    }

    public void setHabilidades(String habilidades) {
        this.habilidades = habilidades;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(
            String portfolioUrl
    ) {
        this.portfolioUrl = portfolioUrl;
    }
}