package com.openfree_api.modules.chat.mapper;

import com.openfree_api.modules.chat.dto.ConversationResponse;
import com.openfree_api.modules.chat.entity.Conversation;

import org.springframework.stereotype.Component;

@Component
public class ConversationMapper {

    public ConversationResponse toResponse(
            Conversation conversation,
            long mensagensNaoLidas
    ) {

        ConversationResponse response =
                new ConversationResponse();

        response.setId(
                conversation.getId()
        );

        response.setCandidaturaId(
                conversation
                        .getCandidatura()
                        .getId()
        );

        response.setVagaId(
                conversation
                        .getCandidatura()
                        .getVaga()
                        .getId()
        );

        response.setVagaTitulo(
                conversation
                        .getCandidatura()
                        .getVaga()
                        .getTitulo()
        );

        response.setEmpresaId(
                conversation
                        .getEmpresa()
                        .getId()
        );

        response.setEmpresaNome(
                conversation
                        .getEmpresa()
                        .getNomeFantasia()
        );

        response.setUsuarioId(
                conversation
                        .getUsuario()
                        .getId()
        );

        response.setUsuarioNome(
                conversation
                        .getUsuario()
                        .getNome()
        );

        response.setStatus(
                conversation.getStatus()
        );

        response.setMensagensNaoLidas(
                mensagensNaoLidas
        );

        response.setCreatedAt(
                conversation.getCreatedAt()
        );

        response.setUpdatedAt(
                conversation.getUpdatedAt()
        );

        return response;
    }
}