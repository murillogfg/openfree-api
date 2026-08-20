package com.openfree_api.modules.companies.service;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.common.validation.CnpjValidator;

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


    public Optional<EmpresaResponse>
    buscarPorId(
            Long id
    ) {

        return empresaRepository
                .findById(id)
                .map(
                        empresaMapper::toResponse
                );
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

        /*
         * Normalizamos antes de qualquer
         * consulta ou persistência.
         *
         * 12.345.678/0001-95
         *
         * vira:
         *
         * 12345678000195
         */
        String cnpjNormalizado =
                CnpjValidator.normalize(
                        request.getCnpj()
                );

        /*
         * Primeiro validamos matematicamente
         * o CNPJ informado.
         */
        if (
                !CnpjValidator.isValid(
                        cnpjNormalizado
                )
        ) {

            throw new BusinessException(
                    "Informe um CNPJ válido."
            );
        }

        /*
         * As validações locais vêm antes da
         * consulta externa.
         *
         * Dessa forma evitamos consultar um
         * serviço externo quando o cadastro
         * já pode ser rejeitado localmente.
         */
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

        String emailUsuarioLogado =
                authentication
                        .getName();

        Usuario owner =
                usuarioRepository
                        .findByEmail(
                                emailUsuarioLogado
                        )
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Usuário autenticado não encontrado."
                                )
                        );

        /*
         * Somente depois das validações locais
         * consultamos se o CNPJ existe na fonte
         * externa.
         *
         * Se a consulta estiver indisponível e
         * retornar null, permitimos o cadastro,
         * deixando o CNPJ como não verificado.
         */
        CnpjLookupResult consultaCnpj =
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

        Empresa empresa =
                empresaMapper
                        .toEntity(
                                request
                        );

        /*
         * Sobrescrevemos os valores do mapper
         * com as versões normalizadas.
         */
        empresa.setCnpj(
                cnpjNormalizado
        );

        empresa.setEmail(
                emailNormalizado
        );

        empresa.setUsuario(
                owner
        );

        /*
         * VERIFIED:
         * consulta realizada e CNPJ encontrado.
         *
         * UNAVAILABLE/null:
         * permitimos o cadastro, porém ele fica
         * aguardando verificação.
         */
        boolean cnpjVerificado =
                consultaCnpj != null
                        && consultaCnpj.verificado();

        empresa.setCnpjVerificado(
                cnpjVerificado
        );

        if (
                cnpjVerificado
        ) {

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