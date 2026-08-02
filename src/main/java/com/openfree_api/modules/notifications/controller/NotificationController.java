package com.openfree_api.modules.notifications.controller;

import com.openfree_api.common.response.ApiResponse;
import com.openfree_api.modules.notifications.dto.NotificationResponse;
import com.openfree_api.modules.notifications.service.NotificationService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(
            NotificationService notificationService
    ) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> listarTodas(
            Authentication authentication
    ) {

        List<NotificationResponse> notificacoes =
                notificationService.listarTodas(authentication);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Notificações listadas com sucesso.",
                        notificacoes
                )
        );
    }

    @GetMapping("/unread")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> listarNaoLidas(
            Authentication authentication
    ) {

        List<NotificationResponse> notificacoes =
                notificationService.listarNaoLidas(authentication);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Notificações não lidas listadas com sucesso.",
                        notificacoes
                )
        );
    }

    @GetMapping("/unread/count")
    public ResponseEntity<ApiResponse<Map<String, Long>>> contarNaoLidas(
            Authentication authentication
    ) {

        long quantidade =
                notificationService.contarNaoLidas(authentication);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Quantidade de notificações não lidas carregada com sucesso.",
                        Map.of(
                                "quantidade",
                                quantidade
                        )
                )
        );
    }

    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<NotificationResponse>> marcarComoLida(
            @PathVariable Long notificationId,
            Authentication authentication
    ) {

        NotificationResponse notification =
                notificationService.marcarComoLida(
                        notificationId,
                        authentication
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Notificação marcada como lida com sucesso.",
                        notification
                )
        );
    }
}