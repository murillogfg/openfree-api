package com.openfree_api.modules.favorites.entity;

import com.openfree_api.modules.jobs.entity.Vaga;
import com.openfree_api.modules.users.entity.Usuario;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "favoritos",
        uniqueConstraints = {
                @UniqueConstraint(
                        columnNames = {
                                "usuario_id",
                                "vaga_id"
                        }
                )
        }
)
public class Favorite {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "usuario_id",
            nullable = false
    )
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "vaga_id",
            nullable = false
    )
    private Vaga vaga;

    @Column(
            name = "created_at",
            nullable = false
    )
    private LocalDateTime createdAt;

    public Favorite() {
        this.createdAt = LocalDateTime.now();
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

    public Vaga getVaga() {
        return vaga;
    }

    public void setVaga(Vaga vaga) {
        this.vaga = vaga;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}