package com.openfree_api.modules.candidaturas.entity;

import com.openfree_api.modules.jobs.entity.Vaga;
import com.openfree_api.modules.users.entity.Usuario;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "candidaturas",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {"vaga_id", "usuario_id"}
                )
        }
)
public class Candidatura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "vaga_id", nullable = false)
    private Vaga vaga;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(length = 1000)
    private String mensagem;

    @Column(precision = 10, scale = 2)
    private BigDecimal valorProposto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusCandidatura status = StatusCandidatura.PENDENTE;

    @Column(nullable = false)
    private Boolean empresaVisualizou = false;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Candidatura() {
    }

    @PrePersist
    public void prePersist() {
        LocalDateTime agora = LocalDateTime.now();

        this.createdAt = agora;
        this.updatedAt = agora;

        if (this.status == null) {
            this.status = StatusCandidatura.PENDENTE;
        }

        if (this.empresaVisualizou == null) {
            this.empresaVisualizou = false;
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Vaga getVaga() {
        return vaga;
    }

    public void setVaga(Vaga vaga) {
        this.vaga = vaga;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public BigDecimal getValorProposto() {
        return valorProposto;
    }

    public void setValorProposto(BigDecimal valorProposto) {
        this.valorProposto = valorProposto;
    }

    public StatusCandidatura getStatus() {
        return status;
    }

    public void setStatus(StatusCandidatura status) {
        this.status = status;
    }

    public Boolean getEmpresaVisualizou() {
        return empresaVisualizou;
    }

    public void setEmpresaVisualizou(Boolean empresaVisualizou) {
        this.empresaVisualizou = empresaVisualizou;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}