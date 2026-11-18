package com.openfree_api.modules.auth.service;

import com.openfree_api.modules.auth.dto.LoginRequest;
import com.openfree_api.modules.auth.dto.LoginResponse;
import com.openfree_api.modules.auth.jwt.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final long EXPIRATION_IN_MILLISECONDS =
            3_600_000L;

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;
    private final UserDetailsService userDetailsService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService,
            RefreshTokenService refreshTokenService,
            UserDetailsService userDetailsService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
        this.userDetailsService = userDetailsService;
    }

    public LoginResponse login(LoginRequest request) {

     Authentication authentication =
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getSenha()
                )
        );

UserDetails userDetails =
        (UserDetails) authentication.getPrincipal();

String token =
        jwtService.gerarToken(userDetails);

String role = authentication
        .getAuthorities()
        .stream()
        .findFirst()
        .map(authority ->
                authority.getAuthority()
                        .replace("ROLE_", "")
        )
        .orElse("FREELANCER");

String refreshToken =
        refreshTokenService.emitir(
                userDetails.getUsername()
        );
return new LoginResponse(
        token,
        "Bearer",
        jwtService.getExpiration(),
        role,
        refreshToken
);
    }

    /*
     * Renova o login usando o refresh token.
     * O token usado deixa de valer e um novo é entregue.
     */
    public LoginResponse renovar(String refreshToken) {

        RefreshTokenService.Renovacao renovacao =
                refreshTokenService.renovar(refreshToken);

        UserDetails userDetails =
                userDetailsService.loadUserByUsername(
                        renovacao.email()
                );

        return new LoginResponse(
                jwtService.gerarToken(userDetails),
                "Bearer",
                jwtService.getExpiration(),
                renovacao.role(),
                renovacao.novoToken()
        );
    }

    public void logout(String refreshToken) {
        refreshTokenService.revogar(refreshToken);
    }
}