package com.openfree_api.modules.auth.security;

import com.openfree_api.modules.users.entity.Usuario;
import com.openfree_api.modules.users.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UserDetailsServiceImpl(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

  @Override
public UserDetails loadUserByUsername(String email)
        throws UsernameNotFoundException {

    Usuario usuario = usuarioRepository
            .findByEmail(email)
            .orElseThrow(() ->
                    new UsernameNotFoundException("Usuário não encontrado.")
            );

    return User.builder()
            .username(usuario.getEmail())
            .password(usuario.getSenha())
            .roles(usuario.getRole().name())
            .build();
}
}
