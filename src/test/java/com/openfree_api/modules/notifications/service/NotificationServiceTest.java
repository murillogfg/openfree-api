package com.openfree_api.modules.notifications.service;

import com.openfree_api.modules.auth.service.UsuarioAuthService;
import com.openfree_api.modules.notifications.dto.NotificationResponse;
import com.openfree_api.modules.notifications.entity.Notification;
import com.openfree_api.modules.notifications.entity.NotificationType;
import com.openfree_api.modules.notifications.mapper.NotificationMapper;
import com.openfree_api.modules.notifications.repository.NotificationRepository;
import com.openfree_api.modules.users.entity.Usuario;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class NotificationServiceTest {

    @Mock
    private NotificationRepository notificationRepository;

    @Mock
    private NotificationMapper notificationMapper;

    @Mock
    private UsuarioAuthService usuarioAuthService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private NotificationService notificationService;

    @Test
    void deveCriarNotificacaoComSucesso() {

        Usuario usuario = new Usuario();
        usuario.setId(10L);
        usuario.setNome("Murillo");
        usuario.setEmail("murillo@email.com");

        String titulo = "Candidatura aceita";

        String mensagem =
                "Sua candidatura foi aceita pela empresa OpenFree.";

        NotificationResponse responseEsperada =
                new NotificationResponse();

        responseEsperada.setId(1L);
        responseEsperada.setTitulo(titulo);
        responseEsperada.setMensagem(mensagem);
        responseEsperada.setTipo(
                NotificationType.SUCCESS
        );
        responseEsperada.setLida(false);

        when(
                notificationRepository.save(
                        any(Notification.class)
                )
        ).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        when(
                notificationMapper.toResponse(
                        any(Notification.class)
                )
        ).thenReturn(responseEsperada);

        NotificationResponse response =
                notificationService.criarNotificacao(
                        usuario,
                        titulo,
                        mensagem,
                        NotificationType.SUCCESS
                );

        assertNotNull(response);

        assertEquals(
                titulo,
                response.getTitulo()
        );

        assertEquals(
                mensagem,
                response.getMensagem()
        );

        assertEquals(
                NotificationType.SUCCESS,
                response.getTipo()
        );

        assertEquals(
                false,
                response.getLida()
        );

        ArgumentCaptor<Notification> captor =
                ArgumentCaptor.forClass(
                        Notification.class
                );

        verify(notificationRepository)
                .save(captor.capture());

        Notification notificationSalva =
                captor.getValue();

        assertSame(
                usuario,
                notificationSalva.getUsuario()
        );

        assertEquals(
                titulo,
                notificationSalva.getTitulo()
        );

        assertEquals(
                mensagem,
                notificationSalva.getMensagem()
        );

        assertEquals(
                NotificationType.SUCCESS,
                notificationSalva.getTipo()
        );

        verify(notificationMapper)
                .toResponse(
                        any(Notification.class)
                );
    }

@Test
void deveListarTodasNotificacoesDoUsuario() {

    Usuario usuario = new Usuario();
    usuario.setId(10L);
    usuario.setEmail("murillo@email.com");

    Notification notification =
            new Notification();

    notification.setUsuario(usuario);
    notification.setTitulo("Nova vaga");
    notification.setMensagem("Uma nova vaga foi publicada.");
    notification.setTipo(NotificationType.INFO);

    NotificationResponse response =
            new NotificationResponse();

    response.setTitulo("Nova vaga");
    response.setMensagem("Uma nova vaga foi publicada.");
    response.setTipo(NotificationType.INFO);

    when(
            usuarioAuthService.getUsuarioLogado(authentication)
    ).thenReturn(usuario);

    when(
            notificationRepository
                    .findByUsuarioIdOrderByCreatedAtDesc(
                            usuario.getId()
                    )
    ).thenReturn(List.of(notification));

    when(
            notificationMapper.toResponse(notification)
    ).thenReturn(response);

    List<NotificationResponse> notificacoes =
            notificationService.listarTodas(
                    authentication
            );

    assertEquals(
            1,
            notificacoes.size()
    );

    assertEquals(
            "Nova vaga",
            notificacoes.get(0).getTitulo()
    );

    verify(notificationRepository)
            .findByUsuarioIdOrderByCreatedAtDesc(
                    usuario.getId()
            );
}
@Test
void deveListarNotificacoesNaoLidas() {

    Usuario usuario = new Usuario();
    usuario.setId(10L);
    usuario.setEmail("murillo@email.com");

    Notification notification = new Notification();
    notification.setUsuario(usuario);
    notification.setTitulo("Nova candidatura");
    notification.setMensagem("Você recebeu uma candidatura.");
    notification.setTipo(NotificationType.INFO);

    NotificationResponse response =
            new NotificationResponse();

    response.setTitulo("Nova candidatura");
    response.setMensagem("Você recebeu uma candidatura.");
    response.setTipo(NotificationType.INFO);

    when(
            usuarioAuthService.getUsuarioLogado(authentication)
    ).thenReturn(usuario);

    when(
            notificationRepository
                    .findByUsuarioIdAndLidaFalseOrderByCreatedAtDesc(
                            usuario.getId()
                    )
    ).thenReturn(List.of(notification));

    when(
            notificationMapper.toResponse(notification)
    ).thenReturn(response);

    List<NotificationResponse> notificacoes =
            notificationService.listarNaoLidas(
                    authentication
            );

    assertEquals(
            1,
            notificacoes.size()
    );

    assertEquals(
            "Nova candidatura",
            notificacoes.get(0).getTitulo()
    );

    verify(notificationRepository)
            .findByUsuarioIdAndLidaFalseOrderByCreatedAtDesc(
                    usuario.getId()
            );
}
@Test
void deveContarNotificacoesNaoLidas() {

    Usuario usuario = new Usuario();
    usuario.setId(10L);
    usuario.setEmail("murillo@email.com");

    when(
            usuarioAuthService.getUsuarioLogado(authentication)
    ).thenReturn(usuario);

    when(
            notificationRepository.countByUsuarioIdAndLidaFalse(
                    usuario.getId()
            )
    ).thenReturn(5L);

    long quantidade =
            notificationService.contarNaoLidas(
                    authentication
            );

    assertEquals(
            5L,
            quantidade
    );

    verify(notificationRepository)
            .countByUsuarioIdAndLidaFalse(
                    usuario.getId()
            );
}


@Test
void deveMarcarNotificacaoComoLida() {

    Long notificationId = 1L;

    Usuario usuario = new Usuario();
    usuario.setId(10L);
    usuario.setEmail("murillo@email.com");

   Notification notification = new Notification();
notification.setUsuario(usuario);
notification.setTitulo("Nova vaga");
notification.setMensagem("Uma nova vaga foi publicada.");
notification.setTipo(NotificationType.INFO);

    NotificationResponse response =
            new NotificationResponse();

    response.setId(notificationId);
    response.setTitulo("Nova vaga");
    response.setLida(true);

    when(
            usuarioAuthService.getUsuarioLogado(authentication)
    ).thenReturn(usuario);

    when(
            notificationRepository.findByIdAndUsuarioId(
                    notificationId,
                    usuario.getId()
            )
    ).thenReturn(Optional.of(notification));

    when(
            notificationRepository.save(any(Notification.class))
    ).thenAnswer(invocation -> invocation.getArgument(0));

    when(
            notificationMapper.toResponse(any(Notification.class))
    ).thenReturn(response);

    NotificationResponse resultado =
            notificationService.marcarComoLida(
                    notificationId,
                    authentication
            );

    assertNotNull(resultado);

    assertEquals(
            true,
            notification.getLida()
    );

    verify(notificationRepository)
            .save(notification);

    verify(notificationMapper)
            .toResponse(notification);
}

}

