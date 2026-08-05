package com.openfree_api.modules.auth.dto;

public class LoginResponse {

    private String token;
    private String tipo;
    private long expiresIn;
    private String role;

    public LoginResponse(
            String token,
            String tipo,
            long expiresIn,
            String role
    ) {
        this.token = token;
        this.tipo = tipo;
        this.expiresIn = expiresIn;
        this.role = role;
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

    public String getRole() {
        return role;
    }
}