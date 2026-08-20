package com.openfree_api.modules.companies.entity;

import com.openfree_api.modules.users.entity.Usuario;

import jakarta.persistence.*;

import java.time.LocalDateTime;


@Entity
@Table(name = "empresas")
public class Empresa {

    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;


    /*
     * Cada empresa pertence a um usuário.
     */
    @OneToOne(
            fetch = FetchType.LAZY,
            optional = false
    )
    @JoinColumn(
            name = "usuario_id",
            nullable = false,
            unique = true
    )
    private Usuario usuario;


    @Column(
            nullable = false,
            length = 150
    )
    private String razaoSocial;


    @Column(
            nullable = false,
            length = 120
    )
    private String nomeFantasia;


    /*
     * Armazenamos somente os 14 números.
     */
    @Column(
            nullable = false,
            unique = true,
            length = 14
    )
    private String cnpj;


    /*
     * Indica se o CNPJ foi efetivamente
     * encontrado/confirmado em uma fonte externa.
     */
    @Column(
            name = "cnpj_verificado",
            nullable = false
    )
    private Boolean cnpjVerificado =
            false;


    /*
     * Momento da última verificação positiva.
     */
    @Column(
            name = "cnpj_verificado_em"
    )
    private LocalDateTime cnpjVerificadoEm;


    @Column(
            nullable = false,
            unique = true,
            length = 150
    )
    private String email;


    @Column(length = 20)
    private String telefone;


    @Column(columnDefinition = "TEXT")
    private String descricao;


    @Column(length = 500)
    private String logo;


    /*
     * Verificação geral da empresa.
     *
     * Não confundir com cnpjVerificado.
     * No futuro pode representar empresa
     * totalmente validada pela OpenFree.
     */
    @Column(nullable = false)
    private Boolean verificada =
            false;


    @Column(nullable = false)
    private Boolean ativa =
            true;


    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;


    @Column(nullable = false)
    private LocalDateTime updatedAt;


    @Column(length = 100)
    private String cidade;


    @Column(length = 2)
    private String estado;


    @Column(length = 255)
    private String site;


    public Empresa() {
    }


    @PrePersist
    public void prePersist() {

        LocalDateTime agora =
                LocalDateTime.now();

        this.createdAt =
                agora;

        this.updatedAt =
                agora;


        if (this.verificada == null) {
            this.verificada = false;
        }


        if (this.cnpjVerificado == null) {
            this.cnpjVerificado = false;
        }


        if (this.ativa == null) {
            this.ativa = true;
        }
    }


    @PreUpdate
    public void preUpdate() {

        this.updatedAt =
                LocalDateTime.now();
    }


    public Long getId() {
        return id;
    }

    public void setId(
            Long id
    ) {
        this.id = id;
    }


    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(
            Usuario usuario
    ) {
        this.usuario = usuario;
    }


    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(
            String razaoSocial
    ) {
        this.razaoSocial = razaoSocial;
    }


    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(
            String nomeFantasia
    ) {
        this.nomeFantasia = nomeFantasia;
    }


    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(
            String cnpj
    ) {
        this.cnpj = cnpj;
    }


    public Boolean getCnpjVerificado() {
        return cnpjVerificado;
    }

    public void setCnpjVerificado(
            Boolean cnpjVerificado
    ) {
        this.cnpjVerificado =
                cnpjVerificado;
    }


    public LocalDateTime getCnpjVerificadoEm() {
        return cnpjVerificadoEm;
    }

    public void setCnpjVerificadoEm(
            LocalDateTime cnpjVerificadoEm
    ) {
        this.cnpjVerificadoEm =
                cnpjVerificadoEm;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(
            String email
    ) {
        this.email = email;
    }


    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(
            String telefone
    ) {
        this.telefone = telefone;
    }


    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(
            String descricao
    ) {
        this.descricao = descricao;
    }


    public String getLogo() {
        return logo;
    }

    public void setLogo(
            String logo
    ) {
        this.logo = logo;
    }


    public Boolean getVerificada() {
        return verificada;
    }

    public void setVerificada(
            Boolean verificada
    ) {
        this.verificada = verificada;
    }


    public Boolean getAtiva() {
        return ativa;
    }

    public void setAtiva(
            Boolean ativa
    ) {
        this.ativa = ativa;
    }


    public LocalDateTime getCreatedAt() {
        return createdAt;
    }


    public LocalDateTime getUpdatedAt() {
        return updatedAt;
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


    public String getSite() {
        return site;
    }

    public void setSite(
            String site
    ) {
        this.site = site;
    }
}