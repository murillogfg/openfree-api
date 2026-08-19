package com.openfree_api.modules.companies.mapper;

import com.openfree_api.modules.companies.dto.CreateEmpresaRequest;
import com.openfree_api.modules.companies.dto.EmpresaResponse;
import com.openfree_api.modules.companies.entity.Empresa;

import org.springframework.stereotype.Component;

@Component
public class EmpresaMapper {

    public Empresa toEntity(
            CreateEmpresaRequest request
    ) {

        Empresa empresa =
                new Empresa();

        empresa.setRazaoSocial(
                request.getRazaoSocial()
        );

        empresa.setNomeFantasia(
                request.getNomeFantasia()
        );

        empresa.setCnpj(
                request.getCnpj()
        );

        empresa.setEmail(
                request.getEmail()
        );

        empresa.setTelefone(
                request.getTelefone()
        );

        empresa.setDescricao(
                request.getDescricao()
        );

        empresa.setLogo(
                request.getLogo()
        );

        empresa.setCidade(
                request.getCidade()
        );

        empresa.setEstado(
                request.getEstado() != null
                        ? request.getEstado()
                            .trim()
                            .toUpperCase()
                        : null
        );

        empresa.setSite(
                request.getSite()
        );

        return empresa;
    }

    public EmpresaResponse toResponse(
            Empresa empresa
    ) {

        EmpresaResponse response =
                new EmpresaResponse();

        response.setId(
                empresa.getId()
        );

        response.setRazaoSocial(
                empresa.getRazaoSocial()
        );

        response.setNomeFantasia(
                empresa.getNomeFantasia()
        );

        response.setCnpj(
                empresa.getCnpj()
        );

        response.setEmail(
                empresa.getEmail()
        );

        response.setTelefone(
                empresa.getTelefone()
        );

        response.setDescricao(
                empresa.getDescricao()
        );

        response.setLogo(
                empresa.getLogo()
        );

        response.setCidade(
                empresa.getCidade()
        );

        response.setEstado(
                empresa.getEstado()
        );

        response.setSite(
                empresa.getSite()
        );

        response.setVerificada(
                empresa.getVerificada()
        );

        response.setAtiva(
                empresa.getAtiva()
        );

        response.setCreatedAt(
                empresa.getCreatedAt()
        );

        response.setUpdatedAt(
                empresa.getUpdatedAt()
        );

        return response;
    }
}