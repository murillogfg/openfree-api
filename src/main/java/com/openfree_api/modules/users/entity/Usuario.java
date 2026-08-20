package com.openfree_api.modules.users.entity;

import com.openfree_api.modules.users.enums.Role;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;

    private String nome;

    @Column(
            nullable = false,
            unique = true
    )
    private String email;

    @Column(nullable = false)
    private String senha;

    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;


    /*
     * ==========================================
     * PERFIL PROFISSIONAL
     * ==========================================
     */

    @Column(length = 120)
    private String tituloProfissional;

    @Column(columnDefinition = "TEXT")
    private String biografia;

    @Column(length = 100)
    private String cidade;

    @Column(length = 2)
    private String estado;

    /*
     * No MVP, as habilidades são armazenadas
     * como texto:
     *
     * "Java, Spring Boot, PostgreSQL"
     *
     * Futuramente podemos transformar isso
     * em relacionamento próprio.
     */
    @Column(columnDefinition = "TEXT")
    private String habilidades;

    @Column(length = 500)
    private String avatarUrl;

    @Column(length = 500)
    private String curriculoUrl;

    @Column(length = 500)
    private String portfolioUrl;


    /*
     * ==========================================
     * TERMOS DE USO / PRIVACIDADE
     * ==========================================
     *
     * Estes campos registram:
     *
     * - quando o usuário aceitou;
     * - qual versão dos termos foi aceita.
     *
     * Permanecem nullable neste momento
     * porque já existem usuários antigos
     * cadastrados no banco.
     */

    @Column(
            name = "terms_accepted_at"
    )
    private LocalDateTime termsAcceptedAt;

    @Column(
            name = "terms_version",
            length = 30
    )
    private String termsVersion;


    /*
     * ==========================================
     * CAMPOS LEGADOS
     * ==========================================
     *
     * Mantidos porque já existiam na entidade.
     * Depois podemos revisar se ainda são
     * necessários.
     */

    @Column(columnDefinition = "TEXT")
    private String bio;

    private String avatar;

    private String curriculo;


    public Usuario() {
    }


    public Long getId() {
        return id;
    }

    public void setId(
            Long id
    ) {
        this.id = id;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(
            String nome
    ) {
        this.nome = nome;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(
            String email
    ) {
        this.email = email;
    }


    public String getSenha() {
        return senha;
    }

    public void setSenha(
            String senha
    ) {
        this.senha = senha;
    }


    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(
            String telefone
    ) {
        this.telefone = telefone;
    }


    public Role getRole() {
        return role;
    }

    public void setRole(
            Role role
    ) {
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

    public void setBiografia(
            String biografia
    ) {
        this.biografia = biografia;
    }


    public String getCidade() {
        return cidade;
    }

    public void setCidade(
            String cidade
    ) {
        this.cidade = cidade;
    }


    public String getEstado() {
        return estado;
    }

    public void setEstado(
            String estado
    ) {
        this.estado = estado;
    }


    public String getHabilidades() {
        return habilidades;
    }

    public void setHabilidades(
            String habilidades
    ) {
        this.habilidades = habilidades;
    }


    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(
            String avatarUrl
    ) {
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


    public LocalDateTime getTermsAcceptedAt() {
        return termsAcceptedAt;
    }

    public void setTermsAcceptedAt(
            LocalDateTime termsAcceptedAt
    ) {
        this.termsAcceptedAt =
                termsAcceptedAt;
    }


    public String getTermsVersion() {
        return termsVersion;
    }

    public void setTermsVersion(
            String termsVersion
    ) {
        this.termsVersion =
                termsVersion;
    }


    public String getBio() {
        return bio;
    }

    public void setBio(
            String bio
    ) {
        this.bio = bio;
    }


    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(
            String avatar
    ) {
        this.avatar = avatar;
    }


    public String getCurriculo() {
        return curriculo;
    }

    public void setCurriculo(
            String curriculo
    ) {
        this.curriculo = curriculo;
    }
}