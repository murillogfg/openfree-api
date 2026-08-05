package com.openfree_api.modules.reviews.entity;

import com.openfree_api.modules.candidaturas.entity.Candidatura;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.users.entity.Usuario;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "avaliacoes",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_avaliacao_candidatura_autor",
                        columnNames = {
                                "candidatura_id",
                                "tipo_autor"
                        }
                )
        }
)
public class Review {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            optional = false,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "candidatura_id",
            nullable = false
    )
    private Candidatura candidatura;

    @ManyToOne(
            optional = false,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "usuario_avaliado_id",
            nullable = false
    )
    private Usuario usuarioAvaliado;

    @ManyToOne(
            optional = false,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "empresa_avaliada_id",
            nullable = false
    )
    private Empresa empresaAvaliada;

    @Enumerated(EnumType.STRING)
    @Column(
            name = "tipo_autor",
            nullable = false,
            length = 20
    )
    private ReviewAuthorType tipoAutor;

    @Column(nullable = false)
    private Integer nota;

    @Column(length = 1500)
    private String comentario;

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    public Review() {
    }

    @PrePersist
    public void prePersist() {

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Candidatura getCandidatura() {
        return candidatura;
    }

    public void setCandidatura(
            Candidatura candidatura
    ) {
        this.candidatura = candidatura;
    }

    public Usuario getUsuarioAvaliado() {
        return usuarioAvaliado;
    }

    public void setUsuarioAvaliado(
            Usuario usuarioAvaliado
    ) {
        this.usuarioAvaliado = usuarioAvaliado;
    }

    public Empresa getEmpresaAvaliada() {
        return empresaAvaliada;
    }

    public void setEmpresaAvaliada(
            Empresa empresaAvaliada
    ) {
        this.empresaAvaliada = empresaAvaliada;
    }

    public ReviewAuthorType getTipoAutor() {
        return tipoAutor;
    }

    public void setTipoAutor(
            ReviewAuthorType tipoAutor
    ) {
        this.tipoAutor = tipoAutor;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(
            String comentario
    ) {
        this.comentario = comentario;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}