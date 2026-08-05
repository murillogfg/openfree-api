package com.openfree_api.modules.reviews.mapper;

import com.openfree_api.modules.reviews.dto.ReviewResponse;
import com.openfree_api.modules.reviews.entity.Review;

import org.springframework.stereotype.Component;

@Component
public class ReviewMapper {

    public ReviewResponse toResponse(
            Review review
    ) {

        ReviewResponse response =
                new ReviewResponse();

        response.setId(
                review.getId()
        );

        response.setCandidaturaId(
                review.getCandidatura().getId()
        );

        response.setVagaId(
                review.getCandidatura()
                        .getVaga()
                        .getId()
        );

        response.setVagaTitulo(
                review.getCandidatura()
                        .getVaga()
                        .getTitulo()
        );

        response.setUsuarioAvaliadoId(
                review.getUsuarioAvaliado().getId()
        );

        response.setUsuarioAvaliadoNome(
                review.getUsuarioAvaliado().getNome()
        );

        response.setEmpresaAvaliadaId(
                review.getEmpresaAvaliada().getId()
        );

        response.setEmpresaAvaliadaNome(
                review.getEmpresaAvaliada()
                        .getNomeFantasia()
        );

        response.setTipoAutor(
                review.getTipoAutor()
        );

        response.setNota(
                review.getNota()
        );

        response.setComentario(
                review.getComentario()
        );

        response.setCreatedAt(
                review.getCreatedAt()
        );

        return response;
    }
}