package com.openfree_api.modules.chat.mapper;

import com.openfree_api.modules.chat.dto.MessageResponse;
import com.openfree_api.modules.chat.entity.Message;

import org.springframework.stereotype.Component;

@Component
public class MessageMapper {

    public MessageResponse toResponse(
            Message message
    ) {

        MessageResponse response =
                new MessageResponse();

        response.setId(
                message.getId()
        );

        response.setRemetenteId(
                message.getRemetente().getId()
        );

        response.setRemetenteNome(
                message.getRemetente().getNome()
        );

        response.setTipoRemetente(
                message.getTipoRemetente()
        );

        response.setConteudo(
                message.getConteudo()
        );

        response.setLida(
                message.getLida()
        );

        response.setCreatedAt(
                message.getCreatedAt()
        );

        response.setReadAt(
                message.getReadAt()
        );

        return response;
    }
}