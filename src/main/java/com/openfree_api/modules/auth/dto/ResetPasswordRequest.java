package com.openfree_api.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class ResetPasswordRequest {

    @NotBlank(
            message = "O token é obrigatório."
    )
    private String token;

    @NotBlank(
            message = "A nova senha é obrigatória."
    )
    @Size(
            min = 6,
            max = 100,
            message = "A senha deve possuir entre 6 e 100 caracteres."
    )
    private String novaSenha;

    public String getToken() {
        return token;
    }

    public void setToken(
            String token
    ) {
        this.token = token;
    }

    public String getNovaSenha() {
        return novaSenha;
    }

    public void setNovaSenha(
            String novaSenha
    ) {
        this.novaSenha = novaSenha;
    }
}