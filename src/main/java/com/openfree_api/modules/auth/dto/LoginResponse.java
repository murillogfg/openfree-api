package com.openfree_api.modules.auth.dto;

public class LoginResponse {

    private String token;
    private String tipo;
    private long expiresIn;

    public LoginResponse(
            String token,
            String tipo,
            long expiresIn
    ) {
        this.token = token;
        this.tipo = tipo;
        this.expiresIn = expiresIn;
    }

    public String getToken() {
        return token;
    }

    public String getTipo() {
        return tipo;
    }

    public long getExpiresIn() {
        return expiresIn;
    }
}