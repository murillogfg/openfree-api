package com.openfree_api.modules.users.service;

import com.openfree_api.modules.users.dto.CreateUsuarioRequest;
import com.openfree_api.modules.users.dto.UpdateUsuarioRequest;
import com.openfree_api.modules.users.dto.UsuarioResponse;
import com.openfree_api.modules.users.entity.Usuario;
import com.openfree_api.modules.users.mapper.UsuarioMapper;
import com.openfree_api.modules.users.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

import com.openfree_api.modules.users.enums.Role;

import org.springframework.security.core.Authentication;
import org.springframework.transaction.annotation.Transactional;

import com.openfree_api.common.exception.BusinessException;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(
            UsuarioRepository usuarioRepository,
            UsuarioMapper usuarioMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.passwordEncoder = passwordEncoder;
    }

    public List<UsuarioResponse> listarTodos() {
        return usuarioRepository.findAll()
                .stream()
                .map(usuarioMapper::toResponse)
                .toList();
    }

    public Optional<UsuarioResponse> buscarPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(usuarioMapper::toResponse);
    }

    public UsuarioResponse criar1(CreateUsuarioRequest request) {

        Usuario usuario = usuarioMapper.toEntity(request);

        String senhaCriptografada =
                passwordEncoder.encode(request.getSenha());

        usuario.setSenha(senhaCriptografada);

        Usuario usuarioSalvo =
                usuarioRepository.save(usuario);

        return usuarioMapper.toResponse(usuarioSalvo);
    }

    public boolean excluir(Long id) {

        if (!usuarioRepository.existsById(id)) {
            return false;
        }

        usuarioRepository.deleteById(id);

        return true;
    }

    public UsuarioResponse criar(CreateUsuarioRequest request) {

    Usuario usuario = usuarioMapper.toEntity(request);

    String senhaCriptografada =
            passwordEncoder.encode(request.getSenha());

    usuario.setSenha(senhaCriptografada);

    // Todo cadastro público começa como freelancer
    usuario.setRole(Role.FREELANCER);

    Usuario usuarioSalvo =
            usuarioRepository.save(usuario);

    return usuarioMapper.toResponse(usuarioSalvo);
}

@Transactional(readOnly = true)
public UsuarioResponse buscarMeuPerfil(
        Authentication authentication
) {

    Usuario usuario =
            usuarioRepository
                    .findByEmail(
                            authentication.getName()
                    )
                    .orElseThrow(() ->
                            new BusinessException(
                                    "Usuário autenticado não encontrado."
                            )
                    );

    return usuarioMapper.toResponse(usuario);
}


@Transactional
public UsuarioResponse atualizarMeuPerfil(
        UpdateUsuarioRequest request,
        Authentication authentication
) {

    Usuario usuario =
            usuarioRepository
                    .findByEmail(
                            authentication.getName()
                    )
                    .orElseThrow(() ->
                            new BusinessException(
                                    "Usuário autenticado não encontrado."
                            )
                    );

    if (request.getNome() != null) {
        usuario.setNome(
                request.getNome().trim()
        );
    }

    if (request.getTelefone() != null) {
        usuario.setTelefone(
                request.getTelefone().trim()
        );
    }

    if (
        request.getTituloProfissional()
                != null
    ) {
        usuario.setTituloProfissional(
                request
                        .getTituloProfissional()
                        .trim()
        );
    }

    if (request.getBiografia() != null) {
        usuario.setBiografia(
                request.getBiografia().trim()
        );
    }

    if (request.getCidade() != null) {
        usuario.setCidade(
                request.getCidade().trim()
        );
    }

    if (request.getEstado() != null) {
        usuario.setEstado(
                request
                        .getEstado()
                        .trim()
                        .toUpperCase()
        );
    }

    if (request.getHabilidades() != null) {
        usuario.setHabilidades(
                request.getHabilidades().trim()
        );
    }

    if (request.getPortfolioUrl() != null) {
        usuario.setPortfolioUrl(
                request
                        .getPortfolioUrl()
                        .trim()
        );
    }

    Usuario atualizado =
            usuarioRepository.save(usuario);

    return usuarioMapper.toResponse(
            atualizado
    );

}

    @Transactional(readOnly = true)
public UsuarioResponse buscarPerfilPublico(
        Long usuarioId
) {

    Usuario usuario =
            usuarioRepository
                    .findById(usuarioId)
                    .orElseThrow(() ->
                            new BusinessException(
                                    "Usuário não encontrado."
                            )
                    );

    return usuarioMapper.toResponse(
            usuario
    );
}
}


