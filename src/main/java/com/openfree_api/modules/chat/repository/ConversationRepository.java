package com.openfree_api.modules.chat.repository;

import com.openfree_api.modules.chat.entity.Conversation;
import com.openfree_api.modules.chat.entity.ConversationStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ConversationRepository
        extends JpaRepository<Conversation, Long> {

    Optional<Conversation> findByCandidaturaId(
            Long candidaturaId
    );

    boolean existsByCandidaturaId(
            Long candidaturaId
    );

    List<Conversation> findByUsuarioIdOrderByUpdatedAtDesc(
            Long usuarioId
    );

    List<Conversation> findByEmpresaIdOrderByUpdatedAtDesc(
            Long empresaId
    );

    List<Conversation> findByUsuarioIdAndStatusOrderByUpdatedAtDesc(
            Long usuarioId,
            ConversationStatus status
    );

    List<Conversation> findByEmpresaIdAndStatusOrderByUpdatedAtDesc(
            Long empresaId,
            ConversationStatus status
    );

    long countByUsuarioIdAndStatus(
        Long usuarioId,
        ConversationStatus status
);
long countByEmpresaIdAndStatus(
        Long empresaId,
        ConversationStatus status
);


    Optional<Conversation> findByIdAndUsuarioId(
            Long conversationId,
            Long usuarioId
    );

    Optional<Conversation> findByIdAndEmpresaId(
            Long conversationId,
            Long empresaId
    );
}