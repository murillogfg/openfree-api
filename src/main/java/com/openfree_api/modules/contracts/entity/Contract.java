package com.openfree_api.modules.contracts.entity;

import com.openfree_api.modules.candidaturas.entity.Candidatura;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.jobs.entity.Vaga;
import com.openfree_api.modules.users.entity.Usuario;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "contratos",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_contrato_candidatura",
                        columnNames = "candidatura_id"
                )
        }
)
public class Contract {

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
            name = "vaga_id",
            nullable = false
    )
    private Vaga vaga;

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
            name = "freelancer_id",
            nullable = false
    )
    private Usuario freelancer;

    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private ContractStatus status =
            ContractStatus.AGUARDANDO_INICIO;

    @Column(nullable = false)
    private Boolean empresaConfirmouConclusao = false;

    @Column(nullable = false)
    private Boolean freelancerConfirmouConclusao = false;

    private LocalDateTime iniciadoAt;

    private LocalDateTime concluidoAt;

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {

        LocalDateTime agora =
                LocalDateTime.now();

        if (status == null) {
            status =
                    ContractStatus.AGUARDANDO_INICIO;
        }

        if (empresaConfirmouConclusao == null) {
            empresaConfirmouConclusao = false;
        }

        if (freelancerConfirmouConclusao == null) {
            freelancerConfirmouConclusao = false;
        }

        createdAt = agora;
        updatedAt = agora;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public Candidatura getCandidatura() {
        return candidatura;
    }

    public void setCandidatura(
            Candidatura candidatura
    ) {
        this.candidatura = candidatura;
    }

    public Vaga getVaga() {
        return vaga;
    }

    public void setVaga(Vaga vaga) {
        this.vaga = vaga;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    public Usuario getFreelancer() {
        return freelancer;
    }

    public void setFreelancer(
            Usuario freelancer
    ) {
        this.freelancer = freelancer;
    }

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public ContractStatus getStatus() {
        return status;
    }

    public void setStatus(
            ContractStatus status
    ) {
        this.status = status;
    }

    public Boolean getEmpresaConfirmouConclusao() {
        return empresaConfirmouConclusao;
    }

    public void setEmpresaConfirmouConclusao(
            Boolean empresaConfirmouConclusao
    ) {
        this.empresaConfirmouConclusao =
                empresaConfirmouConclusao;
    }

    public Boolean getFreelancerConfirmouConclusao() {
        return freelancerConfirmouConclusao;
    }

    public void setFreelancerConfirmouConclusao(
            Boolean freelancerConfirmouConclusao
    ) {
        this.freelancerConfirmouConclusao =
                freelancerConfirmouConclusao;
    }

    public LocalDateTime getIniciadoAt() {
        return iniciadoAt;
    }

    public void setIniciadoAt(
            LocalDateTime iniciadoAt
    ) {
        this.iniciadoAt = iniciadoAt;
    }

    public LocalDateTime getConcluidoAt() {
        return concluidoAt;
    }

    public void setConcluidoAt(
            LocalDateTime concluidoAt
    ) {
        this.concluidoAt = concluidoAt;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setLiberadoEm(LocalDateTime now) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setLiberadoEm'");
    }
}