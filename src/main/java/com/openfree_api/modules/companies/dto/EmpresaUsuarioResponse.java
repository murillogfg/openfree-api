package com.openfree_api.modules.companies.dto;

import com.openfree_api.modules.companies.entity.CargoEmpresa;

public class EmpresaUsuarioResponse {

    private Long usuarioId;
    private String nome;
    private String email;
    private CargoEmpresa cargo;
    private Boolean ativo;

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public CargoEmpresa getCargo() {
        return cargo;
    }

    public void setCargo(CargoEmpresa cargo) {
        this.cargo = cargo;
    }

    public Boolean getAtivo() {
        return ativo;
    }

    public void setAtivo(Boolean ativo) {
        this.ativo = ativo;
    }
}