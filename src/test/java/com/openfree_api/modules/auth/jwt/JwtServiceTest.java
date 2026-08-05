package com.openfree_api.modules.auth.jwt;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.security.core.userdetails.UserDetails;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class JwtServiceTest {

    /*
     * Representa uma chave de 256 bits codificada em Base64.
     * É suficiente para os testes com HMAC.
     */
    private static final String SECRET =
            "MDEyMzQ1Njc4OWFiY2RlZjAxMjM0NTY3ODlhYmNkZWY=";

    private static final long EXPIRATION =
            3_600_000L;

    private JwtService jwtService;

    private UserDetails userDetails;

    @BeforeEach
    void setUp() {

        jwtService =
                new JwtService(
                        SECRET,
                        EXPIRATION
                );

        userDetails =
                mock(UserDetails.class);

        when(
                userDetails.getUsername()
        ).thenReturn(
                "murillo@email.com"
        );
    }

    @Test
    void deveGerarTokenEExtrairDadosComSucesso() {

        long instanteAntes =
                System.currentTimeMillis();

        String token =
                jwtService.gerarToken(userDetails);

        long instanteDepois =
                System.currentTimeMillis();

        assertNotNull(token);
        assertFalse(token.isBlank());

        String emailExtraido =
                jwtService.extrairEmail(token);

        Date expiracaoExtraida =
                jwtService.extrairExpiracao(token);

        assertEquals(
                "murillo@email.com",
                emailExtraido
        );

        assertNotNull(expiracaoExtraida);

        long expiracaoEsperada =
        instanteAntes + EXPIRATION;

long diferenca =
        Math.abs(
                expiracaoExtraida.getTime()
                        - expiracaoEsperada
        );

assertTrue(
        diferenca < 2_000,
        "A expiração deveria estar próxima de 1 hora."
);

        assertEquals(
                EXPIRATION,
                jwtService.getExpiration()
        );
    }
@Test
void deveValidarTokenCorretoParaMesmoUsuario() {

    String token =
            jwtService.gerarToken(userDetails);

    boolean valido =
            jwtService.tokenValido(
                    token,
                    userDetails
            );

    assertTrue(valido);
}
@Test
void deveRetornarFalseQuandoTokenPertencerAOutroUsuario() {

    String token =
            jwtService.gerarToken(userDetails);

    UserDetails outroUsuario =
            mock(UserDetails.class);

    when(
            outroUsuario.getUsername()
    ).thenReturn(
            "outro@email.com"
    );

    boolean valido =
            jwtService.tokenValido(
                    token,
                    outroUsuario
            );

    assertFalse(valido);
}
@Test
void deveExtrairEmailDoToken() {

    String token =
            jwtService.gerarToken(userDetails);

    String email =
            jwtService.extrairEmail(token);

    assertEquals(
            "murillo@email.com",
            email
    );
}
@Test
void deveExtrairDataDeExpiracao() {

    String token =
            jwtService.gerarToken(userDetails);

    Date expiracao =
            jwtService.extrairExpiracao(token);

    assertNotNull(expiracao);

    assertTrue(
            expiracao.after(new Date())
    );
}
@Test
void deveRetornarTempoConfiguradoDeExpiracao() {

    assertEquals(
            EXPIRATION,
            jwtService.getExpiration()
    );
}


}