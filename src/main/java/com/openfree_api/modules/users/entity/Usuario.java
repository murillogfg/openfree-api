package com.openfree_api.modules.users.entity;

import com.openfree_api.modules.users.enums.Role;

import jakarta.persistence.*;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String senha;

    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;
    @Column(length = 120)
    private String tituloProfissional;

    @Column(columnDefinition = "TEXT")
    private String biografia;

    @Column(length = 100)
    private String cidade;

    @Column(length = 2)
    private String estado;

    /*
    * No MVP, as habilidades serão armazenadas como texto:
    * "Java, Spring Boot, PostgreSQL"
    *
    * Depois podemos transformar em uma tabela própria.
    */
    @Column(columnDefinition = "TEXT")
    private String habilidades;

    @Column(length = 500)
    private String avatarUrl;

    @Column(length = 500)
    private String curriculoUrl;

    @Column(length = 500)
    private String portfolioUrl;
        

    public Usuario() {
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

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
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

    public void setTituloProfissional(String tituloProfissional) {
        this.tituloProfissional = tituloProfissional;
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

    public void setCurriculoUrl(String curriculoUrl) {
        this.curriculoUrl = curriculoUrl;
    }

    public String getPortfolioUrl() {
        return portfolioUrl;
    }

    public void setPortfolioUrl(String portfolioUrl) {
        this.portfolioUrl = portfolioUrl;
    }
        
}