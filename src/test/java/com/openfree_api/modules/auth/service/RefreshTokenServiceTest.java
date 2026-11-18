package com.openfree_api.modules.auth.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.BadCredentialsException;

import com.openfree_api.modules.auth.entity.RefreshToken;
import com.openfree_api.modules.auth.repository.RefreshTokenRepository;
import com.openfree_api.modules.users.entity.Usuario;
import com.openfree_api.modules.users.enums.Role;
import com.openfree_api.modules.users.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class RefreshTokenServiceTest {

    @Mock
    private RefreshTokenRepository refreshTokenRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private RefreshTokenService service;

    private Usuario usuario() {
        Usuario usuario = new Usuario();
        usuario.setId(3L);
        usuario.setEmail("ana@email.com");
        usuario.setRole(Role.FREELANCER);
        return usuario;
    }

    private RefreshToken tokenSalvo(Usuario usuario, LocalDateTime expiraEm) {
        RefreshToken token = new RefreshToken();
        token.setUsuario(usuario);
        token.setTokenHash("x");
        token.setCreatedAt(LocalDateTime.now());
        token.setExpiresAt(expiraEm);
        return token;
    }

    @Test
    void guardaSoOHashDoToken() {

        when(usuarioRepository.findByEmailIgnoreCase("ana@email.com"))
                .thenReturn(Optional.of(usuario()));

        String token = service.emitir("ana@email.com");

        ArgumentCaptor<RefreshToken> salvo = ArgumentCaptor.forClass(RefreshToken.class);
        verify(refreshTokenRepository).save(salvo.capture());

        assertNotNull(token);
        assertEquals(64, salvo.getValue().getTokenHash().length());
        assertNotEquals(token, salvo.getValue().getTokenHash());
        assertTrue(salvo.getValue().getExpiresAt().isAfter(LocalDateTime.now().plusDays(29)));
    }

    @Test
    void renovarTrocaOTokenEInvalidaOAnterior() {

        Usuario usuario = usuario();
        RefreshToken atual = tokenSalvo(usuario, LocalDateTime.now().plusDays(5));

        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(atual));

        RefreshTokenService.Renovacao renovacao = service.renovar("token-atual");

        assertTrue(atual.revogado());
        assertEquals("ana@email.com", renovacao.email());
        assertEquals("FREELANCER", renovacao.role());
        assertNotNull(renovacao.novoToken());
        assertNotEquals("token-atual", renovacao.novoToken());
    }

    @Test
    void tokenReutilizadoDerrubaTodasAsSessoes() {

        Usuario usuario = usuario();
        RefreshToken jaUsado = tokenSalvo(usuario, LocalDateTime.now().plusDays(5));
        jaUsado.revogar();

        RefreshToken outraSessao = tokenSalvo(usuario, LocalDateTime.now().plusDays(5));

        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(jaUsado));
        when(refreshTokenRepository.findByUsuarioIdAndRevokedAtIsNull(3L)).thenReturn(List.of(outraSessao));

        assertThrows(BadCredentialsException.class, () -> service.renovar("token-velho"));

        assertTrue(outraSessao.revogado());
    }

    @Test
    void tokenVencidoNaoRenova() {

        RefreshToken vencido = tokenSalvo(usuario(), LocalDateTime.now().minusMinutes(1));

        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.of(vencido));

        assertThrows(BadCredentialsException.class, () -> service.renovar("token-vencido"));
    }

    @Test
    void tokenDesconhecidoNaoRenova() {

        when(refreshTokenRepository.findByTokenHash(anyString())).thenReturn(Optional.empty());

        assertThrows(BadCredentialsException.class, () -> service.renovar("qualquer"));

        verify(refreshTokenRepository, never()).save(any());
    }
}
