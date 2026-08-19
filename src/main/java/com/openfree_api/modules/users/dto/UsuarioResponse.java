package com.openfree_api.modules.users.dto;

import com.openfree_api.modules.users.enums.Role;

public class UsuarioResponse {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private Role role;

    private String tituloProfissional;
    private String biografia;
    private String cidade;
    private String estado;
    private String habilidades;

    private String avatarUrl;
    private String curriculoUrl;
    private String portfolioUrl;

    public UsuarioResponse() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getCurriculoUrl() {
        return curriculoUrl;
    }

    public void setCurriculoUrl(
            String curriculoUrl
    ) {
        this.curriculoUrl = curriculoUrl;
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