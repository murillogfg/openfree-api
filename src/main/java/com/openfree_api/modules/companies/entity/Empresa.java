package com.openfree_api.modules.companies.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

import com.openfree_api.modules.candidaturas.dto.CandidaturaResponse;

@Entity
@Table(name = "empresas")
public class Empresa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 150)
    private String razaoSocial;

    @Column(nullable = false, length = 120)
    private String nomeFantasia;

    @Column(nullable = false, unique = true, length = 18)
    private String cnpj;

    @Column(nullable = false, unique = true, length = 150)
    private String email;

    @Column(length = 20)
    private String telefone;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    private String logo;

    @Column(nullable = false)
    private Boolean verificada = false;

    @Column(nullable = false)
    private Boolean ativa = true;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Empresa() {
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime agora = LocalDateTime.now();

        this.createdAt = agora;
        this.updatedAt = agora;

        if (this.verificada == null) {
            this.verificada = false;
        }

        if (this.ativa == null) {
            this.ativa = true;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
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

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public Boolean getVerificada() {
        return verificada;
    }

    public void setVerificada(Boolean verificada) {
        this.verificada = verificada;
    }

    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(Boolean ativa) {
        this.ativa = ativa;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public CandidaturaResponse getOwner() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getOwner'");
    }
}

