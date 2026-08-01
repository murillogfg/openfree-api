package com.openfree_api.modules.auth.service;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.users.entity.Usuario;
import com.openfree_api.modules.users.repository.UsuarioRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class UsuarioAuthService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioAuthService(
            UsuarioRepository usuarioRepository
    ) {
        this.usuarioRepository = usuarioRepository;
    }

    public Usuario getUsuarioLogado(
            Authentication authentication
    ) {

        String email = authentication.getName();

        return usuarioRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new BusinessException(
                                "Usuário autenticado não encontrado."
                        )
                );
    }
}