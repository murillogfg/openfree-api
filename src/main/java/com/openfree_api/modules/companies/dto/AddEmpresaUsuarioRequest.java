package com.openfree_api.modules.companies.dto;

import com.openfree_api.modules.companies.entity.CargoEmpresa;
import jakarta.validation.constraints.NotNull;

public class AddEmpresaUsuarioRequest {

    @NotNull(message = "O usuário é obrigatório.")
    private Long usuarioId;

    @NotNull(message = "O cargo é obrigatório.")
    private CargoEmpresa cargo;

    public Long getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(Long usuarioId) {
        this.usuarioId = usuarioId;
    }

    public CargoEmpresa getCargo() {
        return cargo;
    }

    public void setCargo(CargoEmpresa cargo) {
        this.cargo = cargo;
    }
}