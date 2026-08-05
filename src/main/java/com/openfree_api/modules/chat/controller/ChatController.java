package com.openfree_api.modules.chat.controller;

import com.openfree_api.common.response.ApiResponse;
import com.openfree_api.modules.chat.dto.ConversationResponse;
import com.openfree_api.modules.chat.dto.CreateConversationRequest;
import com.openfree_api.modules.chat.dto.MessageResponse;
import com.openfree_api.modules.chat.dto.SendMessageRequest;
import com.openfree_api.modules.chat.service.ChatService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
public class ChatController {

    private final ChatService chatService;

    public ChatController(
            ChatService chatService
    ) {
        this.chatService = chatService;
    }

    @PostMapping("/conversations")
    public ResponseEntity<ApiResponse<ConversationResponse>> criarConversa(
            @Valid @RequestBody CreateConversationRequest request,
            Authentication authentication
    ) {

        ConversationResponse conversa =
                chatService.criarConversa(
                        request.getCandidaturaId(),
                        authentication
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Conversa criada com sucesso.",
                                conversa
                        )
                );
    }

    @GetMapping("/conversations")
    public ResponseEntity<ApiResponse<List<ConversationResponse>>> listarConversas(
            Authentication authentication
    ) {

        List<ConversationResponse> conversas =
                chatService.listarMinhasConversas(
                        authentication
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Conversas listadas com sucesso.",
                        conversas
                )
        );
    }

    @GetMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<ApiResponse<List<MessageResponse>>> listarMensagens(
            @PathVariable Long conversationId,
            Authentication authentication
    ) {

        List<MessageResponse> mensagens =
                chatService.listarMensagens(
                        conversationId,
                        authentication
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Mensagens listadas com sucesso.",
                        mensagens
                )
        );
    }

    @PostMapping("/conversations/{conversationId}/messages")
    public ResponseEntity<ApiResponse<MessageResponse>> enviarMensagem(
            @PathVariable Long conversationId,
            @Valid @RequestBody SendMessageRequest request,
            Authentication authentication
    ) {

        MessageResponse mensagem =
                chatService.enviarMensagem(
                        conversationId,
                        request,
                        authentication
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Mensagem enviada com sucesso.",
                                mensagem
                        )
                );
    }

    @PatchMapping("/conversations/{conversationId}/read")
    public ResponseEntity<ApiResponse<Void>> marcarComoLidas(
            @PathVariable Long conversationId,
            Authentication authentication
    ) {

        chatService.marcarMensagensComoLidas(
                conversationId,
                authentication
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Mensagens marcadas como lidas.",
                        null
                )
        );
    }

    @PatchMapping("/conversations/{conversationId}/close")
    public ResponseEntity<ApiResponse<ConversationResponse>> encerrarConversa(
            @PathVariable Long conversationId,
            Authentication authentication
    ) {

        ConversationResponse conversa =
                chatService.encerrarConversa(
                        conversationId,
                        authentication
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Conversa encerrada com sucesso.",
                        conversa
                )
        );
    }
}