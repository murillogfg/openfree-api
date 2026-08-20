package com.openfree_api.modules.users.service;

import com.openfree_api.common.exception.BusinessException;

import com.openfree_api.modules.users.dto.CreateUsuarioRequest;
import com.openfree_api.modules.users.dto.UpdateUsuarioRequest;
import com.openfree_api.modules.users.dto.UsuarioResponse;

import com.openfree_api.modules.users.entity.Usuario;

import com.openfree_api.modules.users.enums.Role;

import com.openfree_api.modules.users.mapper.UsuarioMapper;

import com.openfree_api.modules.users.repository.UsuarioRepository;

import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.util.List;
import java.util.Optional;


@Service
public class UsuarioService {

    /*
     * Versão atualmente válida dos
     * Termos de Uso / Política.
     *
     * Quando houver alteração relevante,
     * podemos mudar para 1.1, 2.0 etc.
     */
    private static final String
            CURRENT_TERMS_VERSION = "1.0";


    private final UsuarioRepository
            usuarioRepository;

    private final UsuarioMapper
            usuarioMapper;

    private final PasswordEncoder
            passwordEncoder;


    public UsuarioService(
            UsuarioRepository usuarioRepository,
            UsuarioMapper usuarioMapper,
            PasswordEncoder passwordEncoder
    ) {

        this.usuarioRepository =
                usuarioRepository;

        this.usuarioMapper =
                usuarioMapper;

        this.passwordEncoder =
                passwordEncoder;
    }


    /*
     * ==========================================
     * LISTAR USUÁRIOS
     * ==========================================
     */

    public List<UsuarioResponse>
    listarTodos() {

        return usuarioRepository
                .findAll()
                .stream()
                .map(
                        usuarioMapper::toResponse
                )
                .toList();
    }


    /*
     * ==========================================
     * BUSCAR POR ID
     * ==========================================
     */

    public Optional<UsuarioResponse>
    buscarPorId(
            Long id
    ) {

        return usuarioRepository
                .findById(id)
                .map(
                        usuarioMapper::toResponse
                );
    }


    /*
     * ==========================================
     * CRIAÇÃO LEGADA
     * ==========================================
     *
     * Mantemos este método porque ele já
     * existia no projeto.
     *
     * Aplicamos a mesma regra dos termos
     * para evitar caminhos inconsistentes.
     */

    public UsuarioResponse criar1(
            CreateUsuarioRequest request
    ) {

        validarAceiteDosTermos(
                request
        );


        Usuario usuario =
                usuarioMapper
                        .toEntity(
                                request
                        );


        prepararNovoUsuario(
                usuario,
                request
        );


        Usuario usuarioSalvo =
                usuarioRepository
                        .save(
                                usuario
                        );


        return usuarioMapper
                .toResponse(
                        usuarioSalvo
                );
    }


    /*
     * ==========================================
     * CADASTRO PÚBLICO
     * ==========================================
     */

    @Transactional
    public UsuarioResponse criar(
            CreateUsuarioRequest request
    ) {

        /*
         * Defesa adicional além do
         * @AssertTrue do DTO.
         */
        validarAceiteDosTermos(
                request
        );


        Usuario usuario =
                usuarioMapper
                        .toEntity(
                                request
                        );


        prepararNovoUsuario(
                usuario,
                request
        );


        Usuario usuarioSalvo =
                usuarioRepository
                        .save(
                                usuario
                        );


        return usuarioMapper
                .toResponse(
                        usuarioSalvo
                );
    }


    /*
     * ==========================================
     * PREPARAR NOVO USUÁRIO
     * ==========================================
     */

    private void prepararNovoUsuario(
            Usuario usuario,
            CreateUsuarioRequest request
    ) {

        String senhaCriptografada =
                passwordEncoder
                        .encode(
                                request.getSenha()
                        );


        usuario.setSenha(
                senhaCriptografada
        );


        /*
         * Todo cadastro público começa
         * como freelancer.
         */
        usuario.setRole(
                Role.FREELANCER
        );


        /*
         * Registro do consentimento.
         */
        usuario.setTermsAcceptedAt(
                LocalDateTime.now()
        );


        usuario.setTermsVersion(
                CURRENT_TERMS_VERSION
        );
    }


    /*
     * ==========================================
     * VALIDAR TERMOS
     * ==========================================
     */

    private void validarAceiteDosTermos(
            CreateUsuarioRequest request
    ) {

        if (
                !Boolean.TRUE.equals(
                        request.getAceitouTermos()
                )
        ) {

            throw new BusinessException(
                    "É necessário aceitar os Termos de Uso e a Política de Privacidade."
            );
        }
    }


    /*
     * ==========================================
     * EXCLUIR
     * ==========================================
     */

    public boolean excluir(
            Long id
    ) {

        if (
                !usuarioRepository
                        .existsById(id)
        ) {

            return false;
        }


        usuarioRepository
                .deleteById(id);


        return true;
    }


    /*
     * ==========================================
     * MEU PERFIL
     * ==========================================
     */

    @Transactional(readOnly = true)
    public UsuarioResponse buscarMeuPerfil(
            Authentication authentication
    ) {

        Usuario usuario =
                buscarUsuarioAutenticado(
                        authentication
                );


        return usuarioMapper
                .toResponse(
                        usuario
                );
    }


    /*
     * ==========================================
     * ATUALIZAR PERFIL
     * ==========================================
     */

    @Transactional
    public UsuarioResponse atualizarMeuPerfil(
            UpdateUsuarioRequest request,
            Authentication authentication
    ) {

        Usuario usuario =
                buscarUsuarioAutenticado(
                        authentication
                );


        if (
                request.getNome()
                != null
        ) {

            usuario.setNome(
                    request
                            .getNome()
                            .trim()
            );
        }


        if (
                request.getTelefone()
                != null
        ) {

            usuario.setTelefone(
                    request
                            .getTelefone()
                            .trim()
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


        if (
                request.getBiografia()
                != null
        ) {

            usuario.setBiografia(
                    request
                            .getBiografia()
                            .trim()
            );
        }


        if (
                request.getCidade()
                != null
        ) {

            usuario.setCidade(
                    request
                            .getCidade()
                            .trim()
            );
        }


        if (
                request.getEstado()
                != null
        ) {

            usuario.setEstado(
                    request
                            .getEstado()
                            .trim()
                            .toUpperCase()
            );
        }


        if (
                request.getHabilidades()
                != null
        ) {

            usuario.setHabilidades(
                    request
                            .getHabilidades()
                            .trim()
            );
        }


        if (
                request.getPortfolioUrl()
                != null
        ) {

            usuario.setPortfolioUrl(
                    request
                            .getPortfolioUrl()
                            .trim()
            );
        }


        Usuario atualizado =
                usuarioRepository
                        .save(
                                usuario
                        );


        return usuarioMapper
                .toResponse(
                        atualizado
                );
    }


    /*
     * ==========================================
     * PERFIL PÚBLICO
     * ==========================================
     */

    @Transactional(readOnly = true)
    public UsuarioResponse buscarPerfilPublico(
            Long usuarioId
    ) {

        Usuario usuario =
                usuarioRepository
                        .findById(
                                usuarioId
                        )
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Usuário não encontrado."
                                )
                        );


        return usuarioMapper
                .toResponse(
                        usuario
                );
    }


    /*
     * ==========================================
     * USUÁRIO AUTENTICADO
     * ==========================================
     */

    private Usuario buscarUsuarioAutenticado(
            Authentication authentication
    ) {

        return usuarioRepository
                .findByEmail(
                        authentication
                                .getName()
                )
                .orElseThrow(() ->
                        new BusinessException(
                                "Usuário autenticado não encontrado."
                        )
                );
    }
}