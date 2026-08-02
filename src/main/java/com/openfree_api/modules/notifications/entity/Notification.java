package com.openfree_api.modules.notifications.entity;

import com.openfree_api.modules.users.entity.Usuario;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacoes")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(
            optional = false,
            fetch = FetchType.LAZY
    )
    @JoinColumn(
            name = "usuario_id",
            nullable = false
    )
    private Usuario usuario;

    @Column(
            nullable = false,
            length = 120
    )
    private String titulo;

    @Column(
            nullable = false,
            length = 1000
    )
    private String mensagem;

    @Enumerated(EnumType.STRING)
    @Column(
            nullable = false,
            length = 20
    )
    private NotificationType tipo = NotificationType.INFO;

    @Column(nullable = false)
    private Boolean lida = false;

    @Column(
            nullable = false,
            updatable = false
    )
    private LocalDateTime createdAt;

    private LocalDateTime readAt;

    public Notification() {
    }

    @PrePersist
    public void prePersist() {

        if (tipo == null) {
            tipo = NotificationType.INFO;
        }

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

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public NotificationType getTipo() {
        return tipo;
    }

    public void setTipo(NotificationType tipo) {
        this.tipo = tipo;
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