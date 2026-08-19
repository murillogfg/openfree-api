package com.openfree_api.modules.auth.service;

import com.openfree_api.common.exception.BusinessException;

import com.openfree_api.modules.companies.dto.EmpresaResponse;
import com.openfree_api.modules.companies.dto.UpdateEmpresaRequest;

import com.openfree_api.modules.companies.entity.Empresa;

import com.openfree_api.modules.companies.mapper.EmpresaMapper;

import com.openfree_api.modules.companies.repository.EmpresaRepository;

import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;


@Service
public class EmpresaAuthService {

    private final EmpresaRepository empresaRepository;

    private final EmpresaMapper empresaMapper;


    public EmpresaAuthService(
            EmpresaRepository empresaRepository,
            EmpresaMapper empresaMapper
    ) {

        this.empresaRepository =
                empresaRepository;

        this.empresaMapper =
                empresaMapper;
    }


    @Transactional(readOnly = true)
    public Empresa getEmpresaLogada(
            Authentication authentication
    ) {

        String email =
                authentication.getName();

        return empresaRepository
                .findByUsuarioEmail(
                        email
                )
                .orElseThrow(() ->
                        new BusinessException(
                                "O usuário autenticado não possui empresa."
                        )
                );
    }


    @Transactional(readOnly = true)
    public EmpresaResponse buscarMinhaEmpresa(
            Authentication authentication
    ) {

        Empresa empresa =
                getEmpresaLogada(
                        authentication
                );

        return empresaMapper.toResponse(
                empresa
        );
    }


    @Transactional
    public EmpresaResponse atualizarMinhaEmpresa(
            UpdateEmpresaRequest request,
            Authentication authentication
    ) {

        Empresa empresa =
                getEmpresaLogada(
                        authentication
                );


        if (
                request.getNomeFantasia()
                != null
        ) {

            empresa.setNomeFantasia(
                    request
                            .getNomeFantasia()
                            .trim()
            );
        }


        if (
                request.getTelefone()
                != null
        ) {

            empresa.setTelefone(
                    request
                            .getTelefone()
                            .trim()
            );
        }


        if (
                request.getDescricao()
                != null
        ) {

            empresa.setDescricao(
                    request
                            .getDescricao()
                            .trim()
            );
        }


        if (
                request.getCidade()
                != null
        ) {

            empresa.setCidade(
                    request
                            .getCidade()
                            .trim()
            );
        }


        if (
                request.getEstado()
                != null
        ) {

            empresa.setEstado(
                    request
                            .getEstado()
                            .trim()
                            .toUpperCase()
            );
        }


        if (
                request.getSite()
                != null
        ) {

            empresa.setSite(
                    request
                            .getSite()
                            .trim()
            );
        }


        Empresa atualizada =
                empresaRepository.save(
                        empresa
                );


        return empresaMapper.toResponse(
                atualizada
        );
    }
}