package com.openfree_api.modules.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import com.openfree_api.common.email.EmailMensagem;
import com.openfree_api.common.email.EmailService;
import com.openfree_api.modules.auth.entity.PasswordResetToken;
import com.openfree_api.modules.auth.repository.PasswordResetTokenRepository;
import com.openfree_api.modules.users.entity.Usuario;
import com.openfree_api.modules.users.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordResetTokenRepository passwordResetTokenRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RefreshTokenService refreshTokenService;

    @Mock
    private EmailService emailService;

    @InjectMocks
    private PasswordResetService service;

    @BeforeEach
    void configurar() {
        ReflectionTestUtils.setField(service, "frontendUrl", "https://openfree.test");
    }

    private Usuario usuario() {
        Usuario usuario = new Usuario();
        usuario.setId(7L);
        usuario.setNome("Ana Souza");
        usuario.setEmail("ana@email.com");
        return usuario;
    }

    @Test
    void enviaEmailComLinkDoToken() {

        when(usuarioRepository.findByEmailIgnoreCase("ana@email.com"))
                .thenReturn(Optional.of(usuario()));
        when(passwordResetTokenRepository.countByUsuarioIdAndCreatedAtAfter(eq(7L), any()))
                .thenReturn(0L);

        service.solicitarRecuperacao("  Ana@Email.com ");

        ArgumentCaptor<PasswordResetToken> tokenSalvo =
                ArgumentCaptor.forClass(PasswordResetToken.class);
        verify(passwordResetTokenRepository).save(tokenSalvo.capture());

        ArgumentCaptor<EmailMensagem> email =
                ArgumentCaptor.forClass(EmailMensagem.class);
        verify(emailService).enviar(email.capture());

        assertEquals("ana@email.com", email.getValue().para());
        assertTrue(email.getValue().texto().contains("https://openfree.test/reset-password?token="));
        assertTrue(email.getValue().html().contains("Olá, Ana!"));

        // o banco guarda só o hash, nunca o token que vai no e-mail
        String token = email.getValue().texto()
                .split("token=")[1].split("\\s")[0];
        assertTrue(!token.equals(tokenSalvo.getValue().getTokenHash()));
        assertEquals(64, tokenSalvo.getValue().getTokenHash().length());
    }

    @Test
    void emailNaoCadastradoNaoEnviaNada() {

        when(usuarioRepository.findByEmailIgnoreCase("ninguem@email.com"))
                .thenReturn(Optional.empty());

        service.solicitarRecuperacao("ninguem@email.com");

        verify(passwordResetTokenRepository, never()).save(any());
        verify(emailService, never()).enviar(any());
    }

    @Test
    void respeitaLimiteDePedidosPorHora() {

        when(usuarioRepository.findByEmailIgnoreCase("ana@email.com"))
                .thenReturn(Optional.of(usuario()));
        when(passwordResetTokenRepository.countByUsuarioIdAndCreatedAtAfter(anyLong(), any()))
                .thenReturn(3L);

        service.solicitarRecuperacao("ana@email.com");

        verify(passwordResetTokenRepository, never()).save(any());
        verify(emailService, never()).enviar(any());
    }
}
