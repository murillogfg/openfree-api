package com.openfree_api.modules.users.service;

import com.openfree_api.common.exception.BusinessException;

import com.openfree_api.modules.companies.dto.CreateEmpresaRequest;
import com.openfree_api.modules.companies.entity.TipoPessoa;
import com.openfree_api.modules.companies.service.EmpresaService;
import com.openfree_api.modules.users.dto.CadastroContratanteRequest;
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

    private final EmpresaService
            empresaService;


    public UsuarioService(
            UsuarioRepository usuarioRepository,
            UsuarioMapper usuarioMapper,
            PasswordEncoder passwordEncoder,
            EmpresaService empresaService
    ) {

        this.empresaService =
                empresaService;

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

        String emailNormalizado =
                normalizarEmail(
                        request.getEmail()
                );

        if (
                emailNormalizado != null
                && usuarioRepository
                        .existsByEmailIgnoreCase(
                                emailNormalizado
                        )
        ) {
            throw new BusinessException(
                    "Este e-mail já está cadastrado."
            );
        }

        request.setEmail(
                emailNormalizado
        );


        /*
         * Defesa adicional além do
         * @AssertTrue do DTO.
         */
        validarAceiteDosTermos(
                request
        );

        boolean contratante =
                "CONTRATANTE".equals(
                        request.getPerfil()
                );

        if (contratante) {
            validarDadosDoContratante(
                    request.getContratante()
            );
        }


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


        /*
         * "Quero contratar": cria o contratante (PF ou PJ)
         * na mesma transação. Se algo falhar (CPF/CNPJ
         * repetido, CNPJ inexistente...), o usuário
         * também não é criado.
         */
        if (contratante) {
            empresaService.criarParaUsuario(
                    usuarioSalvo,
                    montarContratante(
                            usuarioSalvo,
                            request.getContratante()
                    )
            );
        }


        return usuarioMapper
                .toResponse(
                        usuarioSalvo
                );
    }


    private void validarDadosDoContratante(
            CadastroContratanteRequest dados
    ) {

        if (
                dados == null
                || dados.getTipoPessoa() == null
        ) {
            throw new BusinessException(
                    "Informe se você vai contratar como pessoa física ou como empresa."
            );
        }

        if (
                dados.getTipoPessoa() == TipoPessoa.PF
                && isBlank(dados.getCpf())
        ) {
            throw new BusinessException(
                    "O CPF é obrigatório para contratante pessoa física."
            );
        }

        if (dados.getTipoPessoa() == TipoPessoa.PJ) {

            if (isBlank(dados.getCnpj())) {
                throw new BusinessException(
                        "O CNPJ é obrigatório para contratante empresa."
                );
            }

            if (isBlank(dados.getNomeFantasia())) {
                throw new BusinessException(
                        "O nome da empresa é obrigatório."
                );
            }
        }
    }


    /*
     * Converte os dados do cadastro no request usado
     * pela criação do contratante.
     *
     * PF: o nome exibido é o nome da própria pessoa.
     */
    private CreateEmpresaRequest montarContratante(
            Usuario usuario,
            CadastroContratanteRequest dados
    ) {

        CreateEmpresaRequest empresa =
                new CreateEmpresaRequest();

        empresa.setTipoPessoa(
                dados.getTipoPessoa()
        );

        empresa.setEmail(
                usuario.getEmail()
        );

        empresa.setTelefone(
                usuario.getTelefone()
        );

        empresa.setCidade(
                dados.getCidade()
        );

        empresa.setEstado(
                dados.getEstado()
        );

        if (dados.getTipoPessoa() == TipoPessoa.PF) {

            empresa.setCpf(
                    dados.getCpf()
            );

            empresa.setNomeFantasia(
                    usuario.getNome()
            );

            empresa.setRazaoSocial(
                    usuario.getNome()
            );

        } else {

            String nomeFantasia =
                    dados.getNomeFantasia().trim();

            empresa.setCnpj(
                    dados.getCnpj()
            );

            empresa.setNomeFantasia(
                    nomeFantasia
            );

            empresa.setRazaoSocial(
                    isBlank(dados.getRazaoSocial())
                            ? nomeFantasia
                            : dados.getRazaoSocial().trim()
            );
        }

        return empresa;
    }


    private boolean isBlank(
            String valor
    ) {
        return valor == null
                || valor.isBlank();
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

    private String normalizarEmail(
            String email
    ) {
        if (email == null) {
            return null;
        }

        return email
                .trim()
                .toLowerCase();
    }

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


        UsuarioResponse response =
                usuarioMapper
                        .toResponse(
                                usuario
                        );

        /*
         * Perfil público não expõe dados de contato.
         * O contato acontece pelo chat da plataforma.
         */
        response.setEmail(null);
        response.setTelefone(null);

        return response;
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