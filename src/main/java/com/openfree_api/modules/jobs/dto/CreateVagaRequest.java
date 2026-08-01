package com.openfree_api.modules.jobs.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;

public class CreateVagaRequest {

    @NotBlank(message = "O título é obrigatório.")
    @Size(
            max = 120,
            message = "O título deve ter no máximo 120 caracteres."
    )
    private String titulo;

    @NotBlank(message = "A descrição é obrigatória.")
    private String descricao;

    @Size(
            max = 1000,
            message = "Os requisitos devem ter no máximo 1000 caracteres."
    )
    private String requisitos;

    @NotBlank(message = "A cidade é obrigatória.")
    private String cidade;

    @NotBlank(message = "O estado é obrigatório.")
    @Size(
            min = 2,
            max = 2,
            message = "O estado deve conter a sigla com 2 caracteres."
    )
    private String estado;

    @NotNull(message = "O valor é obrigatório.")
    @DecimalMin(
            value = "0.01",
            message = "O valor deve ser maior que zero."
    )
    private BigDecimal valor;

    @NotNull(message = "A quantidade de pessoas é obrigatória.")
    @Min(
            value = 1,
            message = "A quantidade de pessoas deve ser no mínimo 1."
    )
    private Integer quantidadePessoas;

    @NotNull(message = "A data do serviço é obrigatória.")
    @FutureOrPresent(
            message = "A data do serviço não pode estar no passado."
    )
    private LocalDate dataServico;

    @NotNull(message = "O horário de início é obrigatório.")
    private LocalTime horarioInicio;

    @NotNull(message = "O horário de término é obrigatório.")
    private LocalTime horarioFim;

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getRequisitos() {
        return requisitos;
    }

    public void setRequisitos(String requisitos) {
        this.requisitos = requisitos;
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

    public BigDecimal getValor() {
        return valor;
    }

    public void setValor(BigDecimal valor) {
        this.valor = valor;
    }

    public Integer getQuantidadePessoas() {
        return quantidadePessoas;
    }

    public void setQuantidadePessoas(Integer quantidadePessoas) {
        this.quantidadePessoas = quantidadePessoas;
    }

    public LocalDate getDataServico() {
        return dataServico;
    }

    public void setDataServico(LocalDate dataServico) {
        this.dataServico = dataServico;
    }

    public LocalTime getHorarioInicio() {
        return horarioInicio;
    }

    public void setHorarioInicio(LocalTime horarioInicio) {
        this.horarioInicio = horarioInicio;
    }

    public LocalTime getHorarioFim() {
        return horarioFim;
    }

    public void setHorarioFim(LocalTime horarioFim) {
        this.horarioFim = horarioFim;
    }
}