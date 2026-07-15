package com.openfree_api.modules.companies.mapper;

import com.openfree_api.modules.companies.dto.EmpresaUsuarioResponse;
import com.openfree_api.modules.companies.entity.EmpresaUsuario;
import org.springframework.stereotype.Component;

@Component
public class EmpresaUsuarioMapper {

    public EmpresaUsuarioResponse toResponse(EmpresaUsuario empresaUsuario) {

        EmpresaUsuarioResponse response = new EmpresaUsuarioResponse();

        response.setUsuarioId(empresaUsuario.getUsuario().getId());
        response.setNome(empresaUsuario.getUsuario().getNome());
        response.setEmail(empresaUsuario.getUsuario().getEmail());
        response.setCargo(empresaUsuario.getCargo());
        response.setAtivo(empresaUsuario.getAtivo());

        return response;
    }
}