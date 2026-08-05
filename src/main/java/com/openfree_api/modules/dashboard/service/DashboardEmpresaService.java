package com.openfree_api.modules.dashboard.service;

import com.openfree_api.modules.auth.service.EmpresaAuthService;
import com.openfree_api.modules.candidaturas.entity.StatusCandidatura;
import com.openfree_api.modules.candidaturas.repository.CandidaturaRepository;
import com.openfree_api.modules.chat.entity.ConversationStatus;
import com.openfree_api.modules.chat.entity.MessageSenderType;
import com.openfree_api.modules.chat.repository.ConversationRepository;
import com.openfree_api.modules.chat.repository.MessageRepository;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.dashboard.dto.DashboardEmpresaResponse;
import com.openfree_api.modules.jobs.entity.StatusVaga;
import com.openfree_api.modules.jobs.repository.VagaRepository;
import com.openfree_api.modules.reviews.entity.ReviewAuthorType;
import com.openfree_api.modules.reviews.repository.ReviewRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardEmpresaService {

    private final EmpresaAuthService empresaAuthService;
    private final VagaRepository vagaRepository;
    private final CandidaturaRepository candidaturaRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ReviewRepository reviewRepository;

    public DashboardEmpresaService(
            EmpresaAuthService empresaAuthService,
            VagaRepository vagaRepository,
            CandidaturaRepository candidaturaRepository,
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            ReviewRepository reviewRepository
    ) {
        this.empresaAuthService = empresaAuthService;
        this.vagaRepository = vagaRepository;
        this.candidaturaRepository = candidaturaRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional(readOnly = true)
    public DashboardEmpresaResponse dashboard(
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService.getEmpresaLogada(authentication);

        Long empresaId = empresa.getId();

        long vagasPublicadas =
                vagaRepository.countByEmpresaId(empresaId);

        long vagasAbertas =
                vagaRepository.countByEmpresaIdAndStatus(
                        empresaId,
                        StatusVaga.PUBLICADA
                );

        long vagasFinalizadas =
                vagaRepository.countByEmpresaIdAndStatus(
                        empresaId,
                        StatusVaga.FINALIZADA
                );

        long candidaturasRecebidas =
                candidaturaRepository.countByVagaEmpresaId(
                        empresaId
                );

        long candidaturasPendentes =
                candidaturaRepository.countByVagaEmpresaIdAndStatus(
                        empresaId,
                        StatusCandidatura.PENDENTE
                );

        long profissionaisContratados =
                candidaturaRepository.countByVagaEmpresaIdAndStatus(
                        empresaId,
                        StatusCandidatura.ACEITA
                );

        long conversasAtivas =
                conversationRepository.countByEmpresaIdAndStatus(
                        empresaId,
                        ConversationStatus.ATIVA
                );

        long mensagensNaoLidas =
                messageRepository.countMensagensNaoLidasDaEmpresa(
                        empresaId,
                        MessageSenderType.FREELANCER
                );

        Double mediaCalculada =
                reviewRepository.calcularMediaDaEmpresa(
                        empresaId,
                        ReviewAuthorType.FREELANCER
                );

        double avaliacaoMedia =
                mediaCalculada != null
                        ? mediaCalculada
                        : 0.0;

        long totalAvaliacoes =
                reviewRepository.countByEmpresaAvaliadaIdAndTipoAutor(
                        empresaId,
                        ReviewAuthorType.FREELANCER
                );

        double taxaContratacao =
                candidaturasRecebidas == 0
                        ? 0.0
                        : (profissionaisContratados * 100.0)
                        / candidaturasRecebidas;

        DashboardEmpresaResponse response =
                new DashboardEmpresaResponse();

        response.setNomeEmpresa(
                empresa.getNomeFantasia()
        );

        response.setVagasPublicadas(vagasPublicadas);
        response.setVagasAbertas(vagasAbertas);
        response.setVagasFinalizadas(vagasFinalizadas);

        response.setCandidaturasRecebidas(candidaturasRecebidas);
        response.setCandidaturasPendentes(candidaturasPendentes);
        response.setProfissionaisContratados(profissionaisContratados);

        response.setConversasAtivas(conversasAtivas);
        response.setMensagensNaoLidas(mensagensNaoLidas);

        response.setAvaliacaoMedia(avaliacaoMedia);
        response.setTotalAvaliacoes(totalAvaliacoes);
        response.setTaxaContratacao(taxaContratacao);

        return response;
    }
}