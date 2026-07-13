package com.openfree_api.modules.users.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "vagas")
public class Vaga {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;

    private String descricao;

    private String categoria;

    private BigDecimal valor;

    private String cidade;

    private String status;

    private LocalDateTime dataCriacao;

    public Vaga() {
        this.status = "ABERTA";
        this.dataCriacao = LocalDateTime.now();
    }

    // getters e setters
}