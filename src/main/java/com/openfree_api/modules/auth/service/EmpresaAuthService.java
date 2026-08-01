package com.openfree_api.modules.auth.service;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.companies.repository.EmpresaRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class EmpresaAuthService {

    private final EmpresaRepository empresaRepository;

    public EmpresaAuthService(EmpresaRepository empresaRepository) {
        this.empresaRepository = empresaRepository;
    }

    public Empresa getEmpresaLogada(Authentication authentication) {

        String email = authentication.getName();

        return empresaRepository
                .findByUsuarioEmail(email)
                .orElseThrow(() ->
                        new BusinessException(
                                "O usuário autenticado não possui empresa."
                        )
                );
    }

}