package com.openfree_api.modules.reviews.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class CreateReviewRequest {

    @NotNull(message = "A nota é obrigatória.")
    @Min(
            value = 1,
            message = "A nota mínima é 1."
    )
    @Max(
            value = 5,
            message = "A nota máxima é 5."
    )
    private Integer nota;

    @Size(
            max = 1500,
            message = "O comentário deve possuir no máximo 1500 caracteres."
    )
    private String comentario;

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
}