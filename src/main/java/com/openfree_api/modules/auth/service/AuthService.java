package com.openfree_api.modules.auth.service;

import com.openfree_api.modules.auth.dto.LoginRequest;
import com.openfree_api.modules.auth.dto.LoginResponse;
import com.openfree_api.modules.auth.jwt.JwtService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final Logger log =
            LoggerFactory.getLogger(AuthService.class);

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;

    public AuthService(
            AuthenticationManager authenticationManager,
            JwtService jwtService
    ) {
        this.authenticationManager = authenticationManager;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {

        try {

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

            log.info(
                    "Usuário '{}' autenticado com sucesso.",
                    userDetails.getUsername()
            );

            return new LoginResponse(
                    token,
                    "Bearer",
                    jwtService.getExpiration()
            );

        } catch (AuthenticationException exception) {

            log.warn(
                    "Tentativa de login inválida para o e-mail '{}'.",
                    request.getEmail()
            );

            throw exception;
        }
    }
}