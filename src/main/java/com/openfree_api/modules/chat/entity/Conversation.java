package com.openfree_api.modules.chat.entity;

import com.openfree_api.modules.candidaturas.entity.Candidatura;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.users.entity.Usuario;

import jakarta.persistence.*;
import java.time.LocalDateTime;


@Entity
@Table(
        name = "conversas",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_conversa_candidatura",
                        columnNames = "candidatura_id"
                )
        }
)
public class Conversation {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(
            optional = false,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "candidatura_id",
            nullable = false,
            unique = true
    )
    private Candidatura candidatura;

    @ManyToOne(
            optional = false,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "empresa_id",
            nullable = false
    )
    private Empresa empresa;

    @ManyToOne(
            optional = false,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "usuario_id",
            nullable = false
    )
    private Usuario usuario;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private ConversationStatus status =
            ConversationStatus.ATIVA;

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Conversation() {
    }

    @PrePersist
    public void prePersist() {

        LocalDateTime agora =
                LocalDateTime.now();

        if (status == null) {
            status = ConversationStatus.ATIVA;
        }

        if (createdAt == null) {
            createdAt = agora;
        }

        updatedAt = agora;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void encerrar() {
        status = ConversationStatus.ENCERRADA;
    }

    public boolean estaAtiva() {
        return status == ConversationStatus.ATIVA;
    }

    public void atualizarAtividade() {
    this.updatedAt = LocalDateTime.now();
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

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(
            Empresa empresa
    ) {
        this.empresa = empresa;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(
            Usuario usuario
    ) {
        this.usuario = usuario;
    }

    public ConversationStatus getStatus() {
        return status;
    }

    public void setStatus(
            ConversationStatus status
    ) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    }
