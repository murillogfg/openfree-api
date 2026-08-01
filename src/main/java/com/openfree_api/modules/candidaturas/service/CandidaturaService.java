package com.openfree_api.modules.candidaturas.service;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.auth.service.EmpresaAuthService;
import com.openfree_api.modules.auth.service.UsuarioAuthService;
import com.openfree_api.modules.candidaturas.dto.CandidaturaResponse;
import com.openfree_api.modules.candidaturas.dto.CreateCandidaturaRequest;
import com.openfree_api.modules.candidaturas.entity.Candidatura;
import com.openfree_api.modules.candidaturas.entity.StatusCandidatura;
import com.openfree_api.modules.candidaturas.mapper.CandidaturaMapper;
import com.openfree_api.modules.candidaturas.repository.CandidaturaRepository;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.jobs.entity.StatusVaga;
import com.openfree_api.modules.jobs.entity.Vaga;
import com.openfree_api.modules.jobs.repository.VagaRepository;
import com.openfree_api.modules.users.entity.Usuario;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class CandidaturaService {

    private final EmpresaAuthService empresaAuthService;
    private final UsuarioAuthService usuarioAuthService;
    private final CandidaturaRepository candidaturaRepository;
    private final VagaRepository vagaRepository;
    private final CandidaturaMapper candidaturaMapper;

    public CandidaturaService(
            CandidaturaRepository candidaturaRepository,
            VagaRepository vagaRepository,
            CandidaturaMapper candidaturaMapper,
            UsuarioAuthService usuarioAuthService,
            EmpresaAuthService empresaAuthService
    ) {
        this.candidaturaRepository = candidaturaRepository;
        this.vagaRepository = vagaRepository;
        this.candidaturaMapper = candidaturaMapper;
        this.usuarioAuthService = usuarioAuthService;
        this.empresaAuthService = empresaAuthService;
    }

    @Transactional
    public CandidaturaResponse criar(
            Long vagaId,
            CreateCandidaturaRequest request,
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService.getUsuarioLogado(authentication);

        Vaga vaga = vagaRepository
                .findById(vagaId)
                .orElseThrow(() ->
                        new BusinessException(
                                "Vaga não encontrada."
                        )
                );

        if (vaga.getStatus() != StatusVaga.PUBLICADA) {
            throw new BusinessException(
                    "A vaga não está disponível para candidatura."
            );
        }

        if (candidaturaRepository.existsByVagaIdAndUsuarioId(
                vagaId,
                usuario.getId()
        )) {
            throw new BusinessException(
                    "Você já se candidatou para esta vaga."
            );
        }

        Candidatura candidatura =
                candidaturaMapper.toEntity(request);

        candidatura.setUsuario(usuario);
        candidatura.setVaga(vaga);

        Candidatura candidaturaSalva =
                candidaturaRepository.save(candidatura);

        return candidaturaMapper.toResponse(candidaturaSalva);
    }

    @Transactional(readOnly = true)
    public List<CandidaturaResponse> listarPorVaga(
            Long vagaId,
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService.getEmpresaLogada(authentication);

        buscarVagaDaEmpresa(
                vagaId,
                empresa.getId()
        );

        return candidaturaRepository
                .findByVagaId(vagaId)
                .stream()
                .map(candidaturaMapper::toResponse)
                .toList();
    }

    @Transactional
    public CandidaturaResponse aceitar(
            Long candidaturaId,
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService.getEmpresaLogada(authentication);

        Candidatura candidatura =
                buscarCandidaturaDaEmpresa(
                        candidaturaId,
                        empresa.getId()
                );

        if (candidatura.getStatus() != StatusCandidatura.PENDENTE
                && candidatura.getStatus() != StatusCandidatura.VISUALIZADA) {

            throw new BusinessException(
                    "Somente candidaturas pendentes ou visualizadas podem ser aceitas."
            );
        }

        Vaga vaga = candidatura.getVaga();

        if (vaga.getStatus() != StatusVaga.PUBLICADA) {
            throw new BusinessException(
                    "Esta vaga não está mais disponível para aceitar candidatos."
            );
        }

        long quantidadeAceitos =
                candidaturaRepository.countByVagaIdAndStatus(
                        vaga.getId(),
                        StatusCandidatura.ACEITA
                );

        if (quantidadeAceitos >= vaga.getQuantidadePessoas()) {
            throw new BusinessException(
                    "A quantidade necessária de profissionais já foi preenchida."
            );
        }

        candidatura.setStatus(StatusCandidatura.ACEITA);
        candidatura.setEmpresaVisualizou(true);

        Candidatura candidaturaSalva =
                candidaturaRepository.save(candidatura);

        quantidadeAceitos++;

        if (quantidadeAceitos >= vaga.getQuantidadePessoas()) {

            vaga.setStatus(StatusVaga.FINALIZADA);
            vagaRepository.save(vaga);

            List<Candidatura> candidaturasRestantes =
                    candidaturaRepository.findByVagaIdAndStatusIn(
                            vaga.getId(),
                            List.of(
                                    StatusCandidatura.PENDENTE,
                                    StatusCandidatura.VISUALIZADA
                            )
                    );

            candidaturasRestantes.forEach(candidaturaRestante -> {
                candidaturaRestante.setStatus(
                        StatusCandidatura.RECUSADA
                );
                candidaturaRestante.setEmpresaVisualizou(true);
            });

            candidaturaRepository.saveAll(
                    candidaturasRestantes
            );
        }

        return candidaturaMapper.toResponse(
                candidaturaSalva
        );
    }

    @Transactional
    public CandidaturaResponse recusar(
            Long candidaturaId,
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService.getEmpresaLogada(authentication);

        Candidatura candidatura =
                buscarCandidaturaDaEmpresa(
                        candidaturaId,
                        empresa.getId()
                );

        if (candidatura.getStatus() != StatusCandidatura.PENDENTE
                && candidatura.getStatus() != StatusCandidatura.VISUALIZADA) {

            throw new BusinessException(
                    "Somente candidaturas pendentes ou visualizadas podem ser recusadas."
            );
        }

        candidatura.setStatus(
                StatusCandidatura.RECUSADA
        );

        candidatura.setEmpresaVisualizou(true);

        Candidatura candidaturaSalva =
                candidaturaRepository.save(candidatura);

        return candidaturaMapper.toResponse(
                candidaturaSalva
        );
    }

    private Candidatura buscarCandidaturaDaEmpresa(
            Long candidaturaId,
            Long empresaId
    ) {

        return candidaturaRepository
                .findByIdAndVagaEmpresaId(
                        candidaturaId,
                        empresaId
                )
                .orElseThrow(() ->
                        new BusinessException(
                                "Candidatura não encontrada ou não pertence à empresa autenticada."
                        )
                );
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
                .orElseThrow(() ->
                        new BusinessException(
                                "Vaga não encontrada ou não pertence à empresa autenticada."
                        )
                );
    }
}