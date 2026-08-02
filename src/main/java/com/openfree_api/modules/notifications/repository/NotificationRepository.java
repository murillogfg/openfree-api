package com.openfree_api.modules.notifications.repository;

import com.openfree_api.modules.notifications.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NotificationRepository
        extends JpaRepository<Notification, Long> {

    List<Notification> findByUsuarioIdOrderByCreatedAtDesc(
            Long usuarioId
    );

    List<Notification> findByUsuarioIdAndLidaFalseOrderByCreatedAtDesc(
            Long usuarioId
    );

    long countByUsuarioIdAndLidaFalse(
            Long usuarioId
    );

    Optional<Notification> findByIdAndUsuarioId(
            Long notificationId,
            Long usuarioId
    );
}