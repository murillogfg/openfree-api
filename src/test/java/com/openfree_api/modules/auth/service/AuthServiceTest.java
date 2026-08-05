package com.openfree_api.modules.auth.service;

import com.openfree_api.modules.auth.dto.LoginRequest;
import com.openfree_api.modules.auth.dto.LoginResponse;
import com.openfree_api.modules.auth.jwt.JwtService;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import org.springframework.security.authentication.BadCredentialsException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.never;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtService jwtService;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    @InjectMocks
    private AuthService authService;

    @Test
    void deveRealizarLoginComSucesso() {

        LoginRequest request =
                new LoginRequest();

        request.setEmail("murillo@email.com");
        request.setSenha("123456");

        when(
                authenticationManager.authenticate(
                        any(UsernamePasswordAuthenticationToken.class)
                )
        ).thenReturn(authentication);

        when(
                authentication.getPrincipal()
        ).thenReturn(userDetails);

        when(
                jwtService.gerarToken(userDetails)
        ).thenReturn("token-jwt-teste");

        LoginResponse response =
                authService.login(request);

        assertNotNull(response);

        assertEquals(
                "token-jwt-teste",
                response.getToken()
        );

        assertEquals(
                "Bearer",
                response.getTipo()
        );

        assertEquals(
                3_600_000L,
                response.getExpiresIn()
        );

        verify(authenticationManager)
                .authenticate(
                        any(UsernamePasswordAuthenticationToken.class)
                );

        verify(jwtService)
                .gerarToken(userDetails);
    }
@Test
void deveLancarExcecaoQuandoCredenciaisForemInvalidas() {

    LoginRequest request =
            new LoginRequest();

    request.setEmail("murillo@email.com");
    request.setSenha("senha-incorreta");

    when(
            authenticationManager.authenticate(
                    any(UsernamePasswordAuthenticationToken.class)
            )
    ).thenThrow(
            new BadCredentialsException(
                    "Credenciais inválidas."
            )
    );

    BadCredentialsException exception =
            assertThrows(
                    BadCredentialsException.class,
                    () -> authService.login(request)
            );

    assertEquals(
            "Credenciais inválidas.",
            exception.getMessage()
    );

    verify(authenticationManager)
            .authenticate(
                    any(UsernamePasswordAuthenticationToken.class)
            );

    verify(
            jwtService,
            never()
    ).gerarToken(any(UserDetails.class));
}



}