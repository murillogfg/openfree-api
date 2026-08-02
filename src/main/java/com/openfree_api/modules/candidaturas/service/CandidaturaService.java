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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openfree_api.modules.candidaturas.dto.MyApplicationResponse;

import com.openfree_api.modules.notifications.entity.NotificationType;
import com.openfree_api.modules.notifications.service.NotificationService;



import java.util.List;

@Service
public class CandidaturaService {

    private static final Logger log =
            LoggerFactory.getLogger(CandidaturaService.class);

    private final EmpresaAuthService empresaAuthService;
    private final UsuarioAuthService usuarioAuthService;
    private final CandidaturaRepository candidaturaRepository;
    private final VagaRepository vagaRepository;
    private final CandidaturaMapper candidaturaMapper;
        private final NotificationService notificationService;



  public CandidaturaService(
        CandidaturaRepository candidaturaRepository,
        VagaRepository vagaRepository,
        CandidaturaMapper candidaturaMapper,
        UsuarioAuthService usuarioAuthService,
        EmpresaAuthService empresaAuthService,
        NotificationService notificationService
) {
    this.candidaturaRepository = candidaturaRepository;
    this.vagaRepository = vagaRepository;
    this.candidaturaMapper = candidaturaMapper;
    this.usuarioAuthService = usuarioAuthService;
    this.empresaAuthService = empresaAuthService;
    this.notificationService = notificationService;
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
                .orElseThrow(() -> {

                    log.warn(
                            "Usuário '{}' tentou candidatar-se a uma vaga inexistente. vagaId={}",
                            usuario.getEmail(),
                            vagaId
                    );

                    return new BusinessException(
                            "Vaga não encontrada."
                    );
                });

        if (vaga.getStatus() != StatusVaga.PUBLICADA) {

            log.warn(
                    "Usuário '{}' tentou candidatar-se à vaga '{}' (id={}) com status '{}'",
                    usuario.getEmail(),
                    vaga.getTitulo(),
                    vaga.getId(),
                    vaga.getStatus()
            );

            throw new BusinessException(
                    "A vaga não está disponível para candidatura."
            );
        }

        if (candidaturaRepository.existsByVagaIdAndUsuarioId(
                vagaId,
                usuario.getId()
        )) {

            log.warn(
                    "Usuário '{}' tentou candidatar-se novamente à vaga '{}' (id={})",
                    usuario.getEmail(),
                    vaga.getTitulo(),
                    vaga.getId()
            );

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

        log.info(
                "Usuário '{}' candidatou-se à vaga '{}' (vagaId={}, candidaturaId={})",
                usuario.getEmail(),
                vaga.getTitulo(),
                vaga.getId(),
                candidaturaSalva.getId()
        );

        return candidaturaMapper.toResponse(candidaturaSalva);
    }

    @Transactional(readOnly = true)
    public List<MyApplicationResponse> listarMinhasCandidaturas(
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService.getUsuarioLogado(authentication);

        List<MyApplicationResponse> candidaturas =
                candidaturaRepository
                        .findByUsuarioIdOrderByCreatedAtDesc(
                                usuario.getId()
                        )
                        .stream()
                        .map(candidaturaMapper::toMyApplicationResponse)
                        .toList();

        log.info(
                "Usuário '{}' listou {} candidatura(s).",
                usuario.getEmail(),
                candidaturas.size()
        );

        return candidaturas;
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

        List<CandidaturaResponse> candidaturas =
                candidaturaRepository
                        .findByVagaId(vagaId)
                        .stream()
                        .map(candidaturaMapper::toResponse)
                        .toList();

        log.info(
                "Empresa '{}' listou {} candidatura(s) da vaga {}",
                empresa.getNomeFantasia(),
                candidaturas.size(),
                vagaId
        );

        return candidaturas;
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

            log.warn(
                    "Empresa '{}' tentou aceitar a candidatura {} com status '{}'",
                    empresa.getNomeFantasia(),
                    candidatura.getId(),
                    candidatura.getStatus()
            );

            throw new BusinessException(
                    "Somente candidaturas pendentes ou visualizadas podem ser aceitas."
            );
        }

        Vaga vaga = candidatura.getVaga();

        if (vaga.getStatus() != StatusVaga.PUBLICADA) {

            log.warn(
                    "Empresa '{}' tentou aceitar candidatura da vaga '{}' (id={}) com status '{}'",
                    empresa.getNomeFantasia(),
                    vaga.getTitulo(),
                    vaga.getId(),
                    vaga.getStatus()
            );

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

            log.warn(
                    "Empresa '{}' tentou aceitar candidatura acima do limite da vaga '{}' (id={})",
                    empresa.getNomeFantasia(),
                    vaga.getTitulo(),
                    vaga.getId()
            );

            throw new BusinessException(
                    "A quantidade necessária de profissionais já foi preenchida."
            );
        }

        candidatura.setStatus(StatusCandidatura.ACEITA);
        candidatura.setEmpresaVisualizou(true);

        Candidatura candidaturaSalva =
                candidaturaRepository.save(candidatura);

        notificationService.criarNotificacao(
        candidaturaSalva.getUsuario(),
        "Candidatura aceita",
        "Sua candidatura para a vaga \""
                + vaga.getTitulo()
                + "\" foi aceita pela empresa "
                + empresa.getNomeFantasia()
                + ".",
        NotificationType.SUCCESS
);


        log.info(
                "Empresa '{}' aceitou a candidatura {} para a vaga '{}' (vagaId={})",
                empresa.getNomeFantasia(),
                candidaturaSalva.getId(),
                vaga.getTitulo(),
                vaga.getId()
        );

        quantidadeAceitos++;

        if (quantidadeAceitos >= vaga.getQuantidadePessoas()) {

            vaga.setStatus(StatusVaga.FINALIZADA);
            vagaRepository.save(vaga);

            log.info(
                    "Vaga '{}' (id={}) foi finalizada automaticamente após atingir {} profissional(is)",
                    vaga.getTitulo(),
                    vaga.getId(),
                    vaga.getQuantidadePessoas()
            );

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

    notificationService.criarNotificacao(
            candidaturaRestante.getUsuario(),
            "Processo seletivo encerrado",
            "A vaga \""
                    + vaga.getTitulo()
                    + "\" foi preenchida e sua candidatura não foi selecionada.",
            NotificationType.INFO
    );
});

            candidaturaRepository.saveAll(
                    candidaturasRestantes
            );

            if (!candidaturasRestantes.isEmpty()) {

                log.info(
                        "{} candidatura(s) foram recusadas automaticamente para a vaga {}",
                        candidaturasRestantes.size(),
                        vaga.getId()
                );
            }
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

            log.warn(
                    "Empresa '{}' tentou recusar a candidatura {} com status '{}'",
                    empresa.getNomeFantasia(),
                    candidatura.getId(),
                    candidatura.getStatus()
            );

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

notificationService.criarNotificacao(
        candidaturaSalva.getUsuario(),
        "Candidatura recusada",
        "Sua candidatura para a vaga \""
                + candidaturaSalva.getVaga().getTitulo()
                + "\" não foi selecionada pela empresa "
                + empresa.getNomeFantasia()
                + ".",
        NotificationType.WARNING
);

        log.info(
                "Empresa '{}' recusou a candidatura {} para a vaga {}",
                empresa.getNomeFantasia(),
                candidaturaSalva.getId(),
                candidaturaSalva.getVaga().getId()
        );

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
                .orElseThrow(() -> {

                    log.warn(
                            "Candidatura não encontrada ou acesso negado. candidaturaId={}, empresaId={}",
                            candidaturaId,
                            empresaId
                    );

                    return new BusinessException(
                            "Candidatura não encontrada ou não pertence à empresa autenticada."
                    );
                });
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
}