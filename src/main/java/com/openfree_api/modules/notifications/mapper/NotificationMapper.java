package com.openfree_api.modules.notifications.mapper;

import com.openfree_api.modules.notifications.dto.NotificationResponse;
import com.openfree_api.modules.notifications.entity.Notification;

import org.springframework.stereotype.Component;

@Component
public class NotificationMapper {

    public NotificationResponse toResponse(
            Notification notification
    ) {

        NotificationResponse response =
                new NotificationResponse();

        response.setId(
                notification.getId()
        );

        response.setTitulo(
                notification.getTitulo()
        );

        response.setMensagem(
                notification.getMensagem()
        );

        response.setTipo(
                notification.getTipo()
        );

        response.setLida(
                notification.getLida()
        );

        response.setCreatedAt(
                notification.getCreatedAt()
        );

        response.setReadAt(
                notification.getReadAt()
        );

        return response;
    }
}