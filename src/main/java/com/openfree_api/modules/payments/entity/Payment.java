package com.openfree_api.modules.payments.entity;

import com.openfree_api.modules.candidaturas.entity.Candidatura;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.users.entity.Usuario;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "pagamentos",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "uk_pagamento_candidatura",
                        columnNames = "candidatura_id"
                )
        }
)
public class Payment {

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
            name = "freelancer_id",
            nullable = false
    )
    private Usuario freelancer;

    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal valorBruto;

    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal taxaPlataforma;

    @Column(
            nullable = false,
            precision = 12,
            scale = 2
    )
    private BigDecimal valorLiquido;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 30
    )
    private PaymentStatus status;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    private PaymentMethod metodo;

    @Column(length = 150)
    private String externalId;

    private LocalDateTime pagoEm;
    private LocalDateTime liberadoEm;

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    public Payment() {
    }

    @PrePersist
    public void prePersist() {

        LocalDateTime agora =
                LocalDateTime.now();

        if (status == null) {
            status =
                    PaymentStatus.PENDENTE;
        }

        createdAt = agora;
        updatedAt = agora;
    }

    @PreUpdate
    public void preUpdate() {
        updatedAt =
                LocalDateTime.now();
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

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(
            Empresa empresa
    ) {
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

    public BigDecimal getValorBruto() {
        return valorBruto;
    }

    public void setValorBruto(
            BigDecimal valorBruto
    ) {
        this.valorBruto = valorBruto;
    }

    public BigDecimal getTaxaPlataforma() {
        return taxaPlataforma;
    }

    public void setTaxaPlataforma(
            BigDecimal taxaPlataforma
    ) {
        this.taxaPlataforma =
                taxaPlataforma;
    }

    public BigDecimal getValorLiquido() {
        return valorLiquido;
    }

    public void setValorLiquido(
            BigDecimal valorLiquido
    ) {
        this.valorLiquido =
                valorLiquido;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(
            PaymentStatus status
    ) {
        this.status = status;
    }

    public PaymentMethod getMetodo() {
        return metodo;
    }

    public void setMetodo(
            PaymentMethod metodo
    ) {
        this.metodo = metodo;
    }

    public String getExternalId() {
        return externalId;
    }

    public void setExternalId(
            String externalId
    ) {
        this.externalId = externalId;
    }

    public LocalDateTime getPagoEm() {
        return pagoEm;
    }

    public void setPagoEm(
            LocalDateTime pagoEm
    ) {
        this.pagoEm = pagoEm;
    }

    public LocalDateTime getLiberadoEm() {
        return liberadoEm;
    }

    public void setLiberadoEm(
            LocalDateTime liberadoEm
    ) {
        this.liberadoEm = liberadoEm;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
}