package com.openfree_api.modules.chat.service;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.auth.service.EmpresaAuthService;
import com.openfree_api.modules.auth.service.UsuarioAuthService;
import com.openfree_api.modules.candidaturas.entity.Candidatura;
import com.openfree_api.modules.candidaturas.entity.StatusCandidatura;
import com.openfree_api.modules.candidaturas.repository.CandidaturaRepository;
import com.openfree_api.modules.chat.dto.ConversationResponse;
import com.openfree_api.modules.chat.dto.MessageResponse;
import com.openfree_api.modules.chat.dto.SendMessageRequest;
import com.openfree_api.modules.chat.entity.Conversation;
import com.openfree_api.modules.chat.entity.ConversationStatus;
import com.openfree_api.modules.chat.entity.Message;
import com.openfree_api.modules.chat.entity.MessageSenderType;
import com.openfree_api.modules.chat.mapper.ConversationMapper;
import com.openfree_api.modules.chat.mapper.MessageMapper;
import com.openfree_api.modules.chat.repository.ConversationRepository;
import com.openfree_api.modules.chat.repository.MessageRepository;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.users.entity.Usuario;
import com.openfree_api.modules.users.enums.Role;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ChatService {

    private static final Logger log =
            LoggerFactory.getLogger(ChatService.class);

    private final ConversationRepository conversationRepository;
    private final MessageRepository messageRepository;
    private final CandidaturaRepository candidaturaRepository;
    private final ConversationMapper conversationMapper;
    private final MessageMapper messageMapper;
    private final UsuarioAuthService usuarioAuthService;
    private final EmpresaAuthService empresaAuthService;

    public ChatService(
            ConversationRepository conversationRepository,
            MessageRepository messageRepository,
            CandidaturaRepository candidaturaRepository,
            ConversationMapper conversationMapper,
            MessageMapper messageMapper,
            UsuarioAuthService usuarioAuthService,
            EmpresaAuthService empresaAuthService
    ) {
        this.conversationRepository = conversationRepository;
        this.messageRepository = messageRepository;
        this.candidaturaRepository = candidaturaRepository;
        this.conversationMapper = conversationMapper;
        this.messageMapper = messageMapper;
        this.usuarioAuthService = usuarioAuthService;
        this.empresaAuthService = empresaAuthService;
    }

    @Transactional
    public ConversationResponse criarConversa(
            Long candidaturaId,
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService.getEmpresaLogada(authentication);

        Candidatura candidatura =
                candidaturaRepository
                        .findByIdAndVagaEmpresaId(
                                candidaturaId,
                                empresa.getId()
                        )
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Candidatura não encontrada ou não pertence à empresa autenticada."
                                )
                        );

        if (candidatura.getStatus()
                != StatusCandidatura.ACEITA) {

            throw new BusinessException(
                    "O chat somente pode ser iniciado para candidaturas aceitas."
            );
        }

        if (conversationRepository.existsByCandidaturaId(
                candidaturaId
        )) {
            throw new BusinessException(
                    "Já existe uma conversa para esta candidatura."
            );
        }

        Conversation conversation =
                new Conversation();

        conversation.setCandidatura(candidatura);
        conversation.setEmpresa(empresa);
        conversation.setUsuario(
                candidatura.getUsuario()
        );
        conversation.setStatus(
                ConversationStatus.ATIVA
        );

        Conversation conversationSalva =
                conversationRepository.save(
                        conversation
                );

        log.info(
                "Conversa criada. conversationId={}, candidaturaId={}, empresaId={}, usuarioId={}",
                conversationSalva.getId(),
                candidaturaId,
                empresa.getId(),
                candidatura.getUsuario().getId()
        );

        return conversationMapper.toResponse(
                conversationSalva,
                0L
        );
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> listarMinhasConversas(
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService.getUsuarioLogado(
                        authentication
                );

        if (usuario.getRole() == Role.EMPRESA) {
            return listarConversasDaEmpresa(
                    authentication
            );
        }

        return conversationRepository
                .findByUsuarioIdOrderByUpdatedAtDesc(
                        usuario.getId()
                )
                .stream()
                .map(conversation ->
                        mapearConversa(
                                conversation,
                                MessageSenderType.FREELANCER
                        )
                )
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ConversationResponse> listarConversasDaEmpresa(
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService.getEmpresaLogada(
                        authentication
                );

        return conversationRepository
                .findByEmpresaIdOrderByUpdatedAtDesc(
                        empresa.getId()
                )
                .stream()
                .map(conversation ->
                        mapearConversa(
                                conversation,
                                MessageSenderType.EMPRESA
                        )
                )
                .toList();
    }

  @Transactional
public List<MessageResponse> listarMensagens(
        Long conversationId,
        Authentication authentication
) {

    Participante participante =
            validarParticipante(
                    conversationId,
                    authentication
            );

    List<Message> mensagens =
            messageRepository
                    .findByConversationIdOrderByCreatedAtAsc(
                            participante.conversation().getId()
                    );

    List<Message> naoLidasDoOutroParticipante =
            mensagens.stream()
                    .filter(message ->
                            !Boolean.TRUE.equals(
                                    message.getLida()
                            )
                    )
                    .filter(message ->
                            message.getTipoRemetente()
                                    != participante.tipo()
                    )
                    .toList();

    naoLidasDoOutroParticipante.forEach(
            Message::marcarComoLida
    );

    if (!naoLidasDoOutroParticipante.isEmpty()) {

        messageRepository.saveAll(
                naoLidasDoOutroParticipante
        );

        log.info(
                "{} mensagem(ns) marcada(s) automaticamente como lida(s). conversationId={}, usuarioId={}",
                naoLidasDoOutroParticipante.size(),
                conversationId,
                participante.usuario().getId()
        );
    }

    return mensagens
            .stream()
            .map(messageMapper::toResponse)
            .toList();
}
    @Transactional
    public MessageResponse enviarMensagem(
            Long conversationId,
            SendMessageRequest request,
            Authentication authentication
    ) {

        Participante participante =
                validarParticipante(
                        conversationId,
                        authentication
                );

        Conversation conversation =
                participante.conversation();

        if (!conversation.estaAtiva()) {
            throw new BusinessException(
                    "Não é possível enviar mensagens em uma conversa encerrada."
            );
        }

        Message message =
                new Message();

        message.setConversation(conversation);
        message.setRemetente(
                participante.usuario()
        );
        message.setTipoRemetente(
                participante.tipo()
        );
        message.setConteudo(
                request.getConteudo().trim()
        );

        Message messageSalva =
                messageRepository.save(message);

        conversation.atualizarAtividade();
        conversationRepository.save(conversation);

        log.info(
                "Mensagem enviada. messageId={}, conversationId={}, remetenteId={}, tipo={}",
                messageSalva.getId(),
                conversationId,
                participante.usuario().getId(),
                participante.tipo()
        );

        return messageMapper.toResponse(
                messageSalva
        );
    }

    @Transactional
    public void marcarMensagensComoLidas(
            Long conversationId,
            Authentication authentication
    ) {

        Participante participante =
                validarParticipante(
                        conversationId,
                        authentication
                );

        List<Message> mensagens =
                messageRepository
                        .findByConversationIdAndLidaFalseAndTipoRemetenteNot(
                                conversationId,
                                participante.tipo()
                        );

        mensagens.forEach(
                Message::marcarComoLida
        );

        messageRepository.saveAll(mensagens);

        log.info(
                "{} mensagem(ns) marcada(s) como lida(s). conversationId={}, usuarioId={}",
                mensagens.size(),
                conversationId,
                participante.usuario().getId()
        );
    }

    @Transactional
    public ConversationResponse encerrarConversa(
            Long conversationId,
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService.getEmpresaLogada(
                        authentication
                );

        Conversation conversation =
                conversationRepository
                        .findByIdAndEmpresaId(
                                conversationId,
                                empresa.getId()
                        )
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Conversa não encontrada ou não pertence à empresa autenticada."
                                )
                        );

        if (!conversation.estaAtiva()) {
            throw new BusinessException(
                    "A conversa já está encerrada."
            );
        }

        conversation.encerrar();

        Conversation atualizada =
                conversationRepository.save(
                        conversation
                );

        return conversationMapper.toResponse(
                atualizada,
                0L
        );
    }

    private ConversationResponse mapearConversa(
            Conversation conversation,
            MessageSenderType participante
    ) {

        long naoLidas =
                messageRepository
                        .countByConversationIdAndLidaFalseAndTipoRemetenteNot(
                                conversation.getId(),
                                participante
                        );

        return conversationMapper.toResponse(
                conversation,
                naoLidas
        );
    }

    private Participante validarParticipante(
            Long conversationId,
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService.getUsuarioLogado(
                        authentication
                );

        if (usuario.getRole() == Role.EMPRESA) {

            Empresa empresa =
                    empresaAuthService.getEmpresaLogada(
                            authentication
                    );

            Conversation conversation =
                    conversationRepository
                            .findByIdAndEmpresaId(
                                    conversationId,
                                    empresa.getId()
                            )
                            .orElseThrow(() ->
                                    new BusinessException(
                                            "Conversa não encontrada ou acesso não permitido."
                                    )
                            );

            return new Participante(
                    conversation,
                    usuario,
                    MessageSenderType.EMPRESA
            );
        }

        Conversation conversation =
                conversationRepository
                        .findByIdAndUsuarioId(
                                conversationId,
                                usuario.getId()
                        )
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Conversa não encontrada ou acesso não permitido."
                                )
                        );

        return new Participante(
                conversation,
                usuario,
                MessageSenderType.FREELANCER
        );
    }

    private record Participante(
            Conversation conversation,
            Usuario usuario,
            MessageSenderType tipo
    ) {
    }

   @Transactional
public ConversationResponse criarConversaAutomaticamente(
        Candidatura candidatura
) {

    if (
        candidatura.getStatus()
                != StatusCandidatura.ACEITA
    ) {
        throw new BusinessException(
                "O chat somente pode ser criado para candidaturas aceitas."
        );
    }

    return conversationRepository
            .findByCandidaturaId(
                    candidatura.getId()
            )
            .map(conversation -> {

                if (!conversation.estaAtiva()) {

                    conversation.reabrir();

                    conversation =
                            conversationRepository.save(
                                    conversation
                            );

                    log.info(
                            "Conversa reaberta automaticamente. conversationId={}, candidaturaId={}",
                            conversation.getId(),
                            candidatura.getId()
                    );
                }

                return conversationMapper.toResponse(
                        conversation,
                        0L
                );
            })
            .orElseGet(() -> {

                Conversation conversation =
                        new Conversation();

                conversation.setCandidatura(
                        candidatura
                );

                conversation.setEmpresa(
                        candidatura
                                .getVaga()
                                .getEmpresa()
                );

                conversation.setUsuario(
                        candidatura.getUsuario()
                );

                conversation.setStatus(
                        ConversationStatus.ATIVA
                );

                Conversation conversationSalva =
                        conversationRepository.save(
                                conversation
                        );

                log.info(
                        "Conversa criada automaticamente. conversationId={}, candidaturaId={}",
                        conversationSalva.getId(),
                        candidatura.getId()
                );

                return conversationMapper.toResponse(
                        conversationSalva,
                        0L
                );
            });
}
}