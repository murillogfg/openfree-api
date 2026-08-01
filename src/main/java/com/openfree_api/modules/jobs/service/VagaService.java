package com.openfree_api.modules.jobs.service;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.auth.service.EmpresaAuthService;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.companies.repository.EmpresaRepository;
import com.openfree_api.modules.jobs.dto.CreateVagaRequest;
import com.openfree_api.modules.jobs.dto.VagaResponse;
import com.openfree_api.modules.jobs.entity.StatusVaga;
import com.openfree_api.modules.jobs.entity.Vaga;
import com.openfree_api.modules.jobs.mapper.VagaMapper;
import com.openfree_api.modules.jobs.repository.VagaRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class VagaService {

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
    public List<VagaResponse> listarPorEmpresa(Long empresaId) {

        if (!empresaRepository.existsById(empresaId)) {
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
            throw new BusinessException(
                    "A empresa está inativa e não pode criar vagas."
            );
        }

        if (!request.getHorarioFim()
                .isAfter(request.getHorarioInicio())) {

            throw new BusinessException(
                    "O horário de término deve ser posterior ao horário de início."
            );
        }

        Vaga vaga = vagaMapper.toEntity(request);

        vaga.setEmpresa(empresa);

        Vaga vagaSalva = vagaRepository.save(vaga);

        return vagaMapper.toResponse(vagaSalva);
    }

    @Transactional
    public void excluir(
            Long id,
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService.getEmpresaLogada(authentication);

        Vaga vaga = buscarVagaDaEmpresa(id, empresa.getId());

        vagaRepository.delete(vaga);
    }

    @Transactional
    public VagaResponse publicar(
            Long id,
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService.getEmpresaLogada(authentication);

        Vaga vaga = buscarVagaDaEmpresa(id, empresa.getId());

        if (vaga.getStatus() != StatusVaga.RASCUNHO) {
            throw new BusinessException(
                    "Somente vagas em RASCUNHO podem ser publicadas."
            );
        }

        vaga.setStatus(StatusVaga.PUBLICADA);

        Vaga vagaAtualizada = vagaRepository.save(vaga);

        return vagaMapper.toResponse(vagaAtualizada);
    }

    private Vaga buscarVagaDaEmpresa(
            Long vagaId,
            Long empresaId
    ) {

        return vagaRepository
                .findByIdAndEmpresaId(vagaId, empresaId)
                .orElseThrow(() ->
                        new BusinessException(
                                "Vaga não encontrada ou não pertence à empresa autenticada."
                        )
                );
    }
}