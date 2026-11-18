package com.openfree_api.modules.auth.dto;

public class LoginResponse {

    private String token;
    private String tipo;
    private long expiresIn;
    private String role;

    /*
     * Usado para renovar o login sem pedir a senha
     * (POST /auth/refresh). Válido por 30 dias.
     */
    private String refreshToken;

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

    public LoginResponse(
            String token,
            String tipo,
            long expiresIn,
            String role,
            String refreshToken
    ) {
        this(token, tipo, expiresIn, role);
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() {
        return refreshToken;
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