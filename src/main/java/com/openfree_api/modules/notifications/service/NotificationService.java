package com.openfree_api.modules.notifications.service;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.auth.service.UsuarioAuthService;
import com.openfree_api.modules.notifications.dto.NotificationResponse;
import com.openfree_api.modules.notifications.entity.Notification;
import com.openfree_api.modules.notifications.entity.NotificationType;
import com.openfree_api.modules.notifications.mapper.NotificationMapper;
import com.openfree_api.modules.notifications.repository.NotificationRepository;
import com.openfree_api.modules.users.entity.Usuario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class NotificationService {

    private static final Logger log =
            LoggerFactory.getLogger(NotificationService.class);

    private final NotificationRepository notificationRepository;
    private final NotificationMapper notificationMapper;
    private final UsuarioAuthService usuarioAuthService;

    public NotificationService(
            NotificationRepository notificationRepository,
            NotificationMapper notificationMapper,
            UsuarioAuthService usuarioAuthService
    ) {
        this.notificationRepository = notificationRepository;
        this.notificationMapper = notificationMapper;
        this.usuarioAuthService = usuarioAuthService;
    }

    @Transactional
    public NotificationResponse criarNotificacao(
            Usuario usuario,
            String titulo,
            String mensagem,
            NotificationType tipo
    ) {

        Notification notification =
                new Notification();

        notification.setUsuario(usuario);
        notification.setTitulo(titulo);
        notification.setMensagem(mensagem);
        notification.setTipo(tipo);

        Notification notificationSalva =
                notificationRepository.save(notification);

        log.info(
                "Notificação criada para o usuário '{}'. notificationId={}, tipo={}",
                usuario.getEmail(),
                notificationSalva.getId(),
                notificationSalva.getTipo()
        );

        return notificationMapper.toResponse(
                notificationSalva
        );
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> listarTodas(
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService.getUsuarioLogado(authentication);

        List<NotificationResponse> notificacoes =
                notificationRepository
                        .findByUsuarioIdOrderByCreatedAtDesc(
                                usuario.getId()
                        )
                        .stream()
                        .map(notificationMapper::toResponse)
                        .toList();

        log.info(
                "Usuário '{}' listou {} notificação(ões).",
                usuario.getEmail(),
                notificacoes.size()
        );

        return notificacoes;
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> listarNaoLidas(
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService.getUsuarioLogado(authentication);

        List<NotificationResponse> notificacoes =
                notificationRepository
                        .findByUsuarioIdAndLidaFalseOrderByCreatedAtDesc(
                                usuario.getId()
                        )
                        .stream()
                        .map(notificationMapper::toResponse)
                        .toList();

        log.info(
                "Usuário '{}' listou {} notificação(ões) não lida(s).",
                usuario.getEmail(),
                notificacoes.size()
        );

        return notificacoes;
    }

    @Transactional(readOnly = true)
    public long contarNaoLidas(
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService.getUsuarioLogado(authentication);

        return notificationRepository
                .countByUsuarioIdAndLidaFalse(
                        usuario.getId()
                );
    }

    @Transactional
    public NotificationResponse marcarComoLida(
            Long notificationId,
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService.getUsuarioLogado(authentication);

        Notification notification =
                notificationRepository
                        .findByIdAndUsuarioId(
                                notificationId,
                                usuario.getId()
                        )
                        .orElseThrow(() -> {

                            log.warn(
                                    "Usuário '{}' tentou acessar notificação inexistente ou de outro usuário. notificationId={}",
                                    usuario.getEmail(),
                                    notificationId
                            );

                            return new BusinessException(
                                    "Notificação não encontrada."
                            );
                        });

        notification.marcarComoLida();

        Notification notificationAtualizada =
                notificationRepository.save(notification);

        log.info(
                "Usuário '{}' marcou a notificação {} como lida.",
                usuario.getEmail(),
                notificationAtualizada.getId()
        );

        return notificationMapper.toResponse(
                notificationAtualizada
        );
    }
}