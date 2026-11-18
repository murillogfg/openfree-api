package com.openfree_api.modules.companies.service;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.common.validation.CnpjValidator;
import com.openfree_api.common.validation.CpfValidator;

import com.openfree_api.modules.auth.service.EmpresaAuthService;

import com.openfree_api.modules.candidaturas.entity.StatusCandidatura;
import com.openfree_api.modules.candidaturas.repository.CandidaturaRepository;

import com.openfree_api.modules.companies.dto.AddEmpresaUsuarioRequest;
import com.openfree_api.modules.companies.dto.CreateEmpresaRequest;
import com.openfree_api.modules.companies.dto.EmpresaResponse;
import com.openfree_api.modules.companies.dto.EmpresaUsuarioResponse;
import com.openfree_api.modules.companies.dto.UpdateEmpresaRequest;

import com.openfree_api.modules.companies.entity.CargoEmpresa;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.companies.entity.EmpresaUsuario;
import com.openfree_api.modules.companies.entity.TipoPessoa;

import com.openfree_api.modules.companies.mapper.EmpresaMapper;
import com.openfree_api.modules.companies.mapper.EmpresaUsuarioMapper;

import com.openfree_api.modules.companies.repository.EmpresaRepository;
import com.openfree_api.modules.companies.repository.EmpresaUsuarioRepository;

import com.openfree_api.modules.dashboard.dto.DashboardEmpresaResponse;

import com.openfree_api.modules.jobs.entity.StatusVaga;
import com.openfree_api.modules.jobs.repository.VagaRepository;

import com.openfree_api.modules.users.entity.Usuario;
import com.openfree_api.modules.users.enums.Role;
import com.openfree_api.modules.users.repository.UsuarioRepository;

import org.springframework.security.core.Authentication;

import org.springframework.stereotype.Service;

import com.openfree_api.common.cnpj.CnpjLookupResult;
import com.openfree_api.common.cnpj.CnpjLookupService;

import java.time.LocalDateTime;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;


@Service
public class EmpresaService {

    private final EmpresaRepository
            empresaRepository;

    private final EmpresaMapper
            empresaMapper;

    private final EmpresaUsuarioRepository
            empresaUsuarioRepository;

    private final UsuarioRepository
            usuarioRepository;

    private final EmpresaUsuarioMapper
            empresaUsuarioMapper;

    private final VagaRepository
            vagaRepository;

    private final CandidaturaRepository
            candidaturaRepository;

    private final EmpresaAuthService
            empresaAuthService;

    private final CnpjLookupService
        cnpjLookupService;       


    public EmpresaService(
            EmpresaRepository empresaRepository,
            EmpresaMapper empresaMapper,
            EmpresaUsuarioRepository empresaUsuarioRepository,
            UsuarioRepository usuarioRepository,
            EmpresaUsuarioMapper empresaUsuarioMapper,
            VagaRepository vagaRepository,
            CandidaturaRepository candidaturaRepository,
            EmpresaAuthService empresaAuthService,
            CnpjLookupService cnpjLookupService
    ) {

        this.empresaRepository =
                empresaRepository;

        this.empresaMapper =
                empresaMapper;

        this.empresaUsuarioRepository =
                empresaUsuarioRepository;

        this.usuarioRepository =
                usuarioRepository;

        this.empresaUsuarioMapper =
                empresaUsuarioMapper;

        this.vagaRepository =
                vagaRepository;

        this.candidaturaRepository =
                candidaturaRepository;

        this.empresaAuthService =
                empresaAuthService;

        this.cnpjLookupService =
                cnpjLookupService;
    }


    public List<EmpresaResponse>
    listarTodas() {

        return empresaRepository
                .findAll()
                .stream()
                .map(
                        empresaMapper::toResponse
                )
                .toList();
    }


    /*
     * Perfil visto por OUTROS usuários.
     *
     * Contratante pessoa física: e-mail e telefone são
     * dados pessoais (LGPD) e não aparecem aqui. O
     * contato acontece pelo chat da plataforma.
     * O dono vê tudo em /companies/me.
     */
    public Optional<EmpresaResponse>
    buscarPorId(
            Long id
    ) {

        return empresaRepository
                .findById(id)
                .map(empresa -> {

                    EmpresaResponse response =
                            empresaMapper.toResponse(
                                    empresa
                            );

                    if (
                            response != null
                            && empresa.getTipoPessoa()
                                    == TipoPessoa.PF
                    ) {
                        response.setEmail(null);
                        response.setTelefone(null);
                    }

                    return response;
                });
    }


    @Transactional(readOnly = true)
    public EmpresaResponse buscarMinhaEmpresa(
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService
                        .getEmpresaLogada(
                                authentication
                        );

        return empresaMapper
                .toResponse(
                        empresa
                );
    }


    @Transactional
    public EmpresaResponse atualizarMinhaEmpresa(
            UpdateEmpresaRequest request,
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService
                        .getEmpresaLogada(
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
                empresaRepository
                        .save(
                                empresa
                        );


        return empresaMapper
                .toResponse(
                        atualizada
                );
    }


    /*
     * ==========================================
     * CRIAR EMPRESA
     * ==========================================
     */
    @Transactional
    public EmpresaResponse criar(
            CreateEmpresaRequest request,
            Authentication authentication
    ) {

        String emailUsuarioLogado =
                authentication
                        .getName();

        return criarContratante(
                request,
                emailUsuarioLogado,
                () -> usuarioRepository
                        .findByEmail(
                                emailUsuarioLogado
                        )
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Usuário autenticado não encontrado."
                                )
                        )
        );
    }


    /*
     * Usado no cadastro: cria o contratante para um
     * usuário que acabou de ser criado, na mesma
     * transação do cadastro.
     */
    @Transactional
    public EmpresaResponse criarParaUsuario(
            Usuario owner,
            CreateEmpresaRequest request
    ) {

        return criarContratante(
                request,
                owner.getEmail(),
                () -> owner
        );
    }


    /*
     * Fluxo comum de criação do contratante.
     *
     * PJ -> valida CNPJ, consulta a BrasilAPI e pode
     *       ganhar o selo de CNPJ verificado.
     * PF -> valida CPF; o CPF nunca sai nas respostas.
     *
     * A ordem das validações do PJ é a mesma de antes:
     * primeiro as checagens locais, depois a consulta
     * externa.
     */
    private EmpresaResponse criarContratante(
            CreateEmpresaRequest request,
            String emailDono,
            Supplier<Usuario> carregarDono
    ) {

        TipoPessoa tipoPessoa =
                request.getTipoPessoa() != null
                        ? request.getTipoPessoa()
                        : TipoPessoa.PJ;

        String cnpjNormalizado = null;
        String cpfNormalizado = null;

        if (tipoPessoa == TipoPessoa.PJ) {

            cnpjNormalizado =
                    CnpjValidator.normalize(
                            request.getCnpj()
                    );

            if (
                    !CnpjValidator.isValid(
                            cnpjNormalizado
                    )
            ) {
                throw new BusinessException(
                        "Informe um CNPJ válido."
                );
            }

            if (
                    empresaRepository
                            .existsByCnpj(
                                    cnpjNormalizado
                            )
            ) {
                throw new BusinessException(
                        "Já existe uma empresa cadastrada com este CNPJ."
                );
            }

        } else {

            cpfNormalizado =
                    CpfValidator.normalize(
                            request.getCpf()
                    );

            if (
                    !CpfValidator.isValid(
                            cpfNormalizado
                    )
            ) {
                throw new BusinessException(
                        "Informe um CPF válido."
                );
            }

            if (
                    empresaRepository
                            .existsByCpf(
                                    cpfNormalizado
                            )
            ) {
                throw new BusinessException(
                        "Já existe um contratante cadastrado com este CPF."
                );
            }
        }

        String emailNormalizado =
                request
                        .getEmail()
                        .trim()
                        .toLowerCase();

        if (
                empresaRepository
                        .existsByEmail(
                                emailNormalizado
                        )
        ) {
            throw new BusinessException(
                    "Já existe uma empresa cadastrada com este e-mail."
            );
        }

        if (
                empresaRepository
                        .findByUsuarioEmail(
                                emailDono
                        )
                        .isPresent()
        ) {
            throw new BusinessException(
                    "Você já possui uma empresa cadastrada."
            );
        }

        Usuario owner =
                carregarDono.get();

        /*
         * Consulta externa só para PJ. Se o serviço
         * estiver fora do ar (null), o cadastro segue
         * com o CNPJ como não verificado.
         */
        CnpjLookupResult consultaCnpj = null;

        if (tipoPessoa == TipoPessoa.PJ) {

            consultaCnpj =
                    cnpjLookupService.consultar(
                            cnpjNormalizado
                    );

            if (
                    consultaCnpj != null
                            && consultaCnpj.inexistente()
            ) {
                throw new BusinessException(
                        "CNPJ não encontrado."
                );
            }
        }

        Empresa empresa =
                empresaMapper
                        .toEntity(
                                request
                        );

        empresa.setTipoPessoa(
                tipoPessoa
        );

        empresa.setCnpj(
                cnpjNormalizado
        );

        empresa.setCpf(
                cpfNormalizado
        );

        empresa.setEmail(
                emailNormalizado
        );

        empresa.setUsuario(
                owner
        );

        boolean cnpjVerificado =
                consultaCnpj != null
                        && consultaCnpj.verificado();

        empresa.setCnpjVerificado(
                cnpjVerificado
        );

        if (cnpjVerificado) {
            empresa.setCnpjVerificadoEm(
                    LocalDateTime.now()
            );
        }

        Empresa empresaSalva =
                empresaRepository
                        .save(
                                empresa
                        );

        EmpresaUsuario empresaUsuario =
                new EmpresaUsuario();

        empresaUsuario.setEmpresa(
                empresaSalva
        );

        empresaUsuario.setUsuario(
                owner
        );

        empresaUsuario.setCargo(
                CargoEmpresa.OWNER
        );

        empresaUsuario.setAtivo(
                true
        );

        empresaUsuarioRepository
                .save(
                        empresaUsuario
                );

        owner.setRole(
                Role.EMPRESA
        );

        usuarioRepository
                .save(
                        owner
                );

        return empresaMapper
                .toResponse(
                        empresaSalva
                );
    }


    public boolean excluir(
            Long id
    ) {

        if (
                !empresaRepository
                        .existsById(id)
        ) {

            return false;
        }


        empresaRepository
                .deleteById(id);


        return true;
    }


    public List<EmpresaUsuarioResponse>
    listarMembros(
            Long empresaId
    ) {

        if (
                !empresaRepository
                        .existsById(
                                empresaId
                        )
        ) {

            throw new BusinessException(
                    "Empresa não encontrada."
            );
        }


        return empresaUsuarioRepository
                .findByEmpresaId(
                        empresaId
                )
                .stream()
                .map(
                        empresaUsuarioMapper
                                ::toResponse
                )
                .toList();
    }


    public EmpresaUsuarioResponse
    adicionarMembro(
            Long empresaId,
            AddEmpresaUsuarioRequest request
    ) {

        Empresa empresa =
                empresaRepository
                        .findById(
                                empresaId
                        )
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Empresa não encontrada."
                                )
                        );


        Usuario usuario =
                usuarioRepository
                        .findById(
                                request
                                        .getUsuarioId()
                        )
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Usuário não encontrado."
                                )
                        );


        if (
                empresaUsuarioRepository
                        .existsByEmpresaIdAndUsuarioId(
                                empresaId,
                                request.getUsuarioId()
                        )
        ) {

            throw new BusinessException(
                    "Este usuário já pertence à empresa."
            );
        }


        EmpresaUsuario empresaUsuario =
                new EmpresaUsuario();


        empresaUsuario.setEmpresa(
                empresa
        );


        empresaUsuario.setUsuario(
                usuario
        );


        empresaUsuario.setCargo(
                request.getCargo()
        );


        empresaUsuario.setAtivo(
                true
        );


        EmpresaUsuario salvo =
                empresaUsuarioRepository
                        .save(
                                empresaUsuario
                        );


        return empresaUsuarioMapper
                .toResponse(
                        salvo
                );
    }


    public DashboardEmpresaResponse dashboard(
            Long empresaId
    ) {

        if (
                !empresaRepository
                        .existsById(
                                empresaId
                        )
        ) {

            throw new BusinessException(
                    "Empresa não encontrada."
            );
        }


        DashboardEmpresaResponse response =
                new DashboardEmpresaResponse();


        response.setVagasPublicadas(
                vagaRepository
                        .countByEmpresaId(
                                empresaId
                        )
        );


        response.setVagasAbertas(
                vagaRepository
                        .countByEmpresaIdAndStatus(
                                empresaId,
                                StatusVaga.PUBLICADA
                        )
        );


        response.setVagasFinalizadas(
                vagaRepository
                        .countByEmpresaIdAndStatus(
                                empresaId,
                                StatusVaga.FINALIZADA
                        )
        );


        response.setCandidaturasRecebidas(
                candidaturaRepository
                        .countByVagaEmpresaId(
                                empresaId
                        )
        );


        response.setProfissionaisContratados(
                candidaturaRepository
                        .countByVagaEmpresaIdAndStatus(
                                empresaId,
                                StatusCandidatura.ACEITA
                        )
        );


        return response;
    }
}