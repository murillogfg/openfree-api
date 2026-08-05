package com.openfree_api.modules.auth.service;

import com.openfree_api.modules.auth.dto.LoginRequest;
import com.openfree_api.modules.auth.dto.LoginResponse;
import com.openfree_api.modules.auth.jwt.JwtService;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    private static final long EXPIRATION_IN_MILLISECONDS =
            3_600_000L;

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

return new LoginResponse(
        token,
        "Bearer",
        jwtService.getExpiration(),
        role
);
    }
}