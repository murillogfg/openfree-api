package com.openfree_api.modules.chat.entity;

import com.openfree_api.modules.users.entity.Usuario;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "mensagens")
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            optional = false,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "conversa_id",
            nullable = false
    )
    private Conversation conversation;

    @ManyToOne(
            optional = false,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "remetente_id",
            nullable = false
    )
    private Usuario remetente;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private MessageSenderType tipoRemetente;

    @Column(
            nullable = false,
            length = 2000
    )
    private String conteudo;

    @Column(nullable = false)
    private Boolean lida = false;

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    public Message() {
    }

    @PrePersist
    public void prePersist() {

        if (lida == null) {
            lida = false;
        }

        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }

    public void marcarComoLida() {

        if (!Boolean.TRUE.equals(lida)) {
            lida = true;
            readAt = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public void setConversation(
            Conversation conversation
    ) {
        this.conversation = conversation;
    }

    public Usuario getRemetente() {
        return remetente;
    }

    public void setRemetente(
            Usuario remetente
    ) {
        this.remetente = remetente;
    }

    public MessageSenderType getTipoRemetente() {
        return tipoRemetente;
    }

    public void setTipoRemetente(
            MessageSenderType tipoRemetente
    ) {
        this.tipoRemetente = tipoRemetente;
    }

    public String getConteudo() {
        return conteudo;
    }

    public void setConteudo(
            String conteudo
    ) {
        this.conteudo = conteudo;
    }

    public Boolean getLida() {
        return lida;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getReadAt() {
        return readAt;
    }
}