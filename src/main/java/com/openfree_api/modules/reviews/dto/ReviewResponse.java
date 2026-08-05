package com.openfree_api.modules.reviews.dto;

import com.openfree_api.modules.reviews.entity.ReviewAuthorType;

import java.time.LocalDateTime;

public class ReviewResponse {

    private Long id;
    private Long candidaturaId;
    private Long vagaId;
    private String vagaTitulo;

    private Long usuarioAvaliadoId;
    private String usuarioAvaliadoNome;

    private Long empresaAvaliadaId;
    private String empresaAvaliadaNome;

    private ReviewAuthorType tipoAutor;
    private Integer nota;
    private String comentario;
    private LocalDateTime createdAt;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCandidaturaId() {
        return candidaturaId;
    }

    public void setCandidaturaId(Long candidaturaId) {
        this.candidaturaId = candidaturaId;
    }

    public Long getVagaId() {
        return vagaId;
    }

    public void setVagaId(Long vagaId) {
        this.vagaId = vagaId;
    }

    public String getVagaTitulo() {
        return vagaTitulo;
    }

    public void setVagaTitulo(String vagaTitulo) {
        this.vagaTitulo = vagaTitulo;
    }

    public Long getUsuarioAvaliadoId() {
        return usuarioAvaliadoId;
    }

    public void setUsuarioAvaliadoId(Long usuarioAvaliadoId) {
        this.usuarioAvaliadoId = usuarioAvaliadoId;
    }

    public String getUsuarioAvaliadoNome() {
        return usuarioAvaliadoNome;
    }

    public void setUsuarioAvaliadoNome(String usuarioAvaliadoNome) {
        this.usuarioAvaliadoNome = usuarioAvaliadoNome;
    }

    public Long getEmpresaAvaliadaId() {
        return empresaAvaliadaId;
    }

    public void setEmpresaAvaliadaId(Long empresaAvaliadaId) {
        this.empresaAvaliadaId = empresaAvaliadaId;
    }

    public String getEmpresaAvaliadaNome() {
        return empresaAvaliadaNome;
    }

    public void setEmpresaAvaliadaNome(String empresaAvaliadaNome) {
        this.empresaAvaliadaNome = empresaAvaliadaNome;
    }

    public ReviewAuthorType getTipoAutor() {
        return tipoAutor;
    }

    public void setTipoAutor(ReviewAuthorType tipoAutor) {
        this.tipoAutor = tipoAutor;
    }

    public Integer getNota() {
        return nota;
    }

    public void setNota(Integer nota) {
        this.nota = nota;
    }

    public String getComentario() {
        return comentario;
    }

    public void setComentario(String comentario) {
        this.comentario = comentario;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}