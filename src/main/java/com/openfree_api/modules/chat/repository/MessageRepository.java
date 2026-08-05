package com.openfree_api.modules.chat.repository;

import com.openfree_api.modules.chat.entity.Message;
import com.openfree_api.modules.chat.entity.MessageSenderType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface MessageRepository
        extends JpaRepository<Message, Long> {

    List<Message> findByConversationIdOrderByCreatedAtAsc(
            Long conversationId
    );

    List<Message> findByConversationIdAndLidaFalseOrderByCreatedAtAsc(
            Long conversationId
    );

    List<Message> findByConversationIdAndLidaFalseAndTipoRemetenteNot(
            Long conversationId,
            MessageSenderType tipoRemetente
    );

    long countByConversationIdAndLidaFalseAndTipoRemetenteNot(
            Long conversationId,
            MessageSenderType tipoRemetente
    );

    @Query("""
        select count(m)
        from Message m
        where m.conversation.usuario.id = :usuarioId
          and m.lida = false
          and m.tipoRemetente = :tipoRemetente
        """)
long countMensagensNaoLidasDoFreelancer(
        @Param("usuarioId") Long usuarioId,
        @Param("tipoRemetente") MessageSenderType tipoRemetente
);
@Query("""
        select count(m)
        from Message m
        where m.conversation.empresa.id = :empresaId
          and m.lida = false
          and m.tipoRemetente = :tipoRemetente
        """)
long countMensagensNaoLidasDaEmpresa(
        @Param("empresaId") Long empresaId,
        @Param("tipoRemetente") MessageSenderType tipoRemetente
);

}