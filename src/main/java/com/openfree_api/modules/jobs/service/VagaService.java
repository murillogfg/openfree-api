package com.openfree_api.modules.jobs.service;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.common.response.PageResponse;
import com.openfree_api.modules.auth.service.EmpresaAuthService;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.companies.repository.EmpresaRepository;
import com.openfree_api.modules.jobs.dto.CreateVagaRequest;
import com.openfree_api.modules.jobs.dto.JobFilterRequest;
import com.openfree_api.modules.jobs.dto.VagaResponse;
import com.openfree_api.modules.jobs.entity.StatusVaga;
import com.openfree_api.modules.jobs.entity.Vaga;
import com.openfree_api.modules.jobs.mapper.VagaMapper;
import com.openfree_api.modules.jobs.repository.VagaRepository;
import com.openfree_api.modules.jobs.specification.VagaSpecification;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Service
public class VagaService {

    private static final Logger log =
            LoggerFactory.getLogger(VagaService.class);

    private static final Set<String> CAMPOS_ORDENACAO_PERMITIDOS =
            Set.of(
                    "id",
                    "titulo",
                    "cidade",
                    "estado",
                    "valor",
                    "dataServico",
                    "createdAt",
                    "updatedAt",
                    "status"
            );

    private final VagaRepository vagaRepository;
    private final EmpresaRepository empresaRepository;
    private final EmpresaAuthService empresaAuthService;
    private final VagaMapper vagaMapper;

    public VagaService(
            VagaRepository vagaRepository,
            EmpresaRepository empresaRepository,
            EmpresaAuthService empresaAuthService,
            VagaMapper vagaMapper
    ) {
        this.vagaRepository = vagaRepository;
        this.empresaRepository = empresaRepository;
        this.empresaAuthService = empresaAuthService;
        this.vagaMapper = vagaMapper;
    }


    @Transactional(readOnly = true)
public List<VagaResponse> listarTodas() {

    return vagaRepository.findAll()
            .stream()
            .map(vagaMapper::toResponse)
            .toList();
}

@Transactional(readOnly = true)
public Optional<VagaResponse> buscarPorId(Long id) {

    return vagaRepository.findById(id)
            .map(vagaMapper::toResponse);
}




    @Transactional(readOnly = true)
    public PageResponse<VagaResponse> buscar(
            JobFilterRequest filtro,
            Pageable pageable
    ) {

        validarPaginacaoEOrdenacao(pageable);

        Page<Vaga> vagas = vagaRepository.findAll(
                VagaSpecification.filtro(filtro),
                pageable
        );

        return PageResponse.from(
                vagas,
                vagaMapper::toResponse
        );
    }

    @Transactional(readOnly = true)
    public List<VagaResponse> listarPorEmpresa(Long empresaId) {

        if (!empresaRepository.existsById(empresaId)) {

            log.warn(
                    "Tentativa de listar vagas de empresa inexistente. empresaId={}",
                    empresaId
            );

            throw new BusinessException(
                    "Empresa não encontrada."
            );
        }

        return vagaRepository.findByEmpresaId(empresaId)
                .stream()
                .map(vagaMapper::toResponse)
                .toList();
    }

    @Transactional
    public VagaResponse criar(
            CreateVagaRequest request,
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService.getEmpresaLogada(authentication);

        if (!Boolean.TRUE.equals(empresa.getAtiva())) {

            log.warn(
                    "Empresa inativa tentou criar uma vaga. empresaId={}, nome='{}'",
                    empresa.getId(),
                    empresa.getNomeFantasia()
            );

            throw new BusinessException(
                    "A empresa está inativa e não pode criar vagas."
            );
        }

        if (!request.getHorarioFim()
                .isAfter(request.getHorarioInicio())) {

            log.warn(
                    "Empresa '{}' tentou criar vaga com horário inválido. inicio={}, fim={}",
                    empresa.getNomeFantasia(),
                    request.getHorarioInicio(),
                    request.getHorarioFim()
            );

            throw new BusinessException(
                    "O horário de término deve ser posterior ao horário de início."
            );
        }

        Vaga vaga = vagaMapper.toEntity(request);
        vaga.setEmpresa(empresa);

        Vaga vagaSalva =
                vagaRepository.save(vaga);

        log.info(
                "Empresa '{}' criou a vaga '{}' (id={})",
                empresa.getNomeFantasia(),
                vagaSalva.getTitulo(),
                vagaSalva.getId()
        );

        return vagaMapper.toResponse(vagaSalva);
    }

    @Transactional
    public void excluir(
            Long id,
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService.getEmpresaLogada(authentication);

        Vaga vaga =
                buscarVagaDaEmpresa(
                        id,
                        empresa.getId()
                );

        log.info(
                "Empresa '{}' excluiu a vaga '{}' (id={})",
                empresa.getNomeFantasia(),
                vaga.getTitulo(),
                vaga.getId()
        );

        vagaRepository.delete(vaga);
    }

    @Transactional
    public VagaResponse publicar(
            Long id,
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService.getEmpresaLogada(authentication);

        Vaga vaga =
                buscarVagaDaEmpresa(
                        id,
                        empresa.getId()
                );

        if (vaga.getStatus() != StatusVaga.RASCUNHO) {

            log.warn(
                    "Empresa '{}' tentou publicar a vaga '{}' (id={}) com status '{}'",
                    empresa.getNomeFantasia(),
                    vaga.getTitulo(),
                    vaga.getId(),
                    vaga.getStatus()
            );

            throw new BusinessException(
                    "Somente vagas em RASCUNHO podem ser publicadas."
            );
        }

        vaga.setStatus(StatusVaga.PUBLICADA);

        Vaga vagaAtualizada =
                vagaRepository.save(vaga);

        log.info(
                "Empresa '{}' publicou a vaga '{}' (id={})",
                empresa.getNomeFantasia(),
                vagaAtualizada.getTitulo(),
                vagaAtualizada.getId()
        );

        return vagaMapper.toResponse(vagaAtualizada);
    }

    private Vaga buscarVagaDaEmpresa(
            Long vagaId,
            Long empresaId
    ) {

        return vagaRepository
                .findByIdAndEmpresaId(
                        vagaId,
                        empresaId
                )
                .orElseThrow(() -> {

                    log.warn(
                            "Vaga não encontrada ou acesso negado. vagaId={}, empresaId={}",
                            vagaId,
                            empresaId
                    );

                    return new BusinessException(
                            "Vaga não encontrada ou não pertence à empresa autenticada."
                    );
                });
    }

    private void validarPaginacaoEOrdenacao(
            Pageable pageable
    ) {

        if (pageable.getPageSize() > 100) {

            log.warn(
                    "Tamanho de página acima do permitido: {}",
                    pageable.getPageSize()
            );

            throw new BusinessException(
                    "O tamanho máximo permitido é de 100 registros por página."
            );
        }

        for (Sort.Order order : pageable.getSort()) {

            String campo = order.getProperty();

            if (!CAMPOS_ORDENACAO_PERMITIDOS.contains(campo)) {

                log.warn(
                        "Tentativa de ordenação por campo inválido: '{}'",
                        campo
                );

                throw new BusinessException(
                        "Campo de ordenação inválido: '"
                                + campo
                                + "'. Campos permitidos: "
                                + String.join(
                                        ", ",
                                        CAMPOS_ORDENACAO_PERMITIDOS
                                )
                                + "."
                );
            }
        }
    }
}