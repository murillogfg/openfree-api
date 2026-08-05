package com.openfree_api.modules.dashboard.service;

import com.openfree_api.modules.auth.service.UsuarioAuthService;
import com.openfree_api.modules.candidaturas.entity.StatusCandidatura;
import com.openfree_api.modules.candidaturas.repository.CandidaturaRepository;
import com.openfree_api.modules.chat.entity.ConversationStatus;
import com.openfree_api.modules.chat.entity.MessageSenderType;
import com.openfree_api.modules.chat.repository.ConversationRepository;
import com.openfree_api.modules.chat.repository.MessageRepository;
import com.openfree_api.modules.dashboard.dto.DashboardFreelancerResponse;
import com.openfree_api.modules.favorites.repository.FavoriteRepository;
import com.openfree_api.modules.notifications.repository.NotificationRepository;
import com.openfree_api.modules.reviews.entity.ReviewAuthorType;
import com.openfree_api.modules.reviews.repository.ReviewRepository;
import com.openfree_api.modules.users.entity.Usuario;
import com.openfree_api.modules.dashboard.service.DashboardFreelancerService;



import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class DashboardFreelancerService {

    private final UsuarioAuthService usuarioAuthService;
    private final CandidaturaRepository candidaturaRepository;
    private final FavoriteRepository favoriteRepository;
    private final NotificationRepository notificationRepository;
    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final ReviewRepository reviewRepository;

    public DashboardFreelancerService(
            UsuarioAuthService usuarioAuthService,
            CandidaturaRepository candidaturaRepository,
            FavoriteRepository favoriteRepository,
            NotificationRepository notificationRepository,
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            ReviewRepository reviewRepository
    ) {
        this.usuarioAuthService = usuarioAuthService;
        this.candidaturaRepository = candidaturaRepository;
        this.favoriteRepository = favoriteRepository;
        this.notificationRepository = notificationRepository;
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.reviewRepository = reviewRepository;
    }

    @Transactional(readOnly = true)
    public DashboardFreelancerResponse dashboard(
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService.getUsuarioLogado(authentication);

        Long usuarioId = usuario.getId();

        long candidaturasEnviadas =
                candidaturaRepository.countByUsuarioId(usuarioId);

        long pendentes =
                candidaturaRepository.countByUsuarioIdAndStatus(
                        usuarioId,
                        StatusCandidatura.PENDENTE
                );

        long aceitas =
                candidaturaRepository.countByUsuarioIdAndStatus(
                        usuarioId,
                        StatusCandidatura.ACEITA
                );

        long recusadas =
                candidaturaRepository.countByUsuarioIdAndStatus(
                        usuarioId,
                        StatusCandidatura.RECUSADA
                );

        long favoritos =
                favoriteRepository.countByUsuarioId(usuarioId);

        long notificacoesNaoLidas =
                notificationRepository.countByUsuarioIdAndLidaFalse(
                        usuarioId
                );

        long conversasAtivas =
                conversationRepository.countByUsuarioIdAndStatus(
                        usuarioId,
                        ConversationStatus.ATIVA
                );

        long mensagensNaoLidas =
                messageRepository.countMensagensNaoLidasDoFreelancer(
                        usuarioId,
                        MessageSenderType.EMPRESA
                );

        Double mediaCalculada =
                reviewRepository.calcularMediaDoUsuario(
                        usuarioId,
                        ReviewAuthorType.EMPRESA
                );

        double avaliacaoMedia =
                mediaCalculada != null
                        ? mediaCalculada
                        : 0.0;

        long totalAvaliacoes =
                reviewRepository.countByUsuarioAvaliadoIdAndTipoAutor(
                        usuarioId,
                        ReviewAuthorType.EMPRESA
                );

        DashboardFreelancerResponse response =
                new DashboardFreelancerResponse();

        response.setCandidaturasEnviadas(candidaturasEnviadas);
        response.setPendentes(pendentes);
        response.setAceitas(aceitas);
        response.setRecusadas(recusadas);

        // Ainda não existe o status CONCLUIDA.
        response.setTrabalhosConcluidos(0L);

        response.setFavoritos(favoritos);
        response.setNotificacoesNaoLidas(notificacoesNaoLidas);
        response.setConversasAtivas(conversasAtivas);
        response.setMensagensNaoLidas(mensagensNaoLidas);
        response.setAvaliacaoMedia(avaliacaoMedia);
        response.setTotalAvaliacoes(totalAvaliacoes);

        return response;
    }
}