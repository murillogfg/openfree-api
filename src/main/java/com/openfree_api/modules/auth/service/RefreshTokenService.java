package com.openfree_api.modules.auth.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.HexFormat;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.openfree_api.modules.auth.entity.RefreshToken;
import com.openfree_api.modules.auth.repository.RefreshTokenRepository;
import com.openfree_api.modules.users.entity.Usuario;
import com.openfree_api.modules.users.repository.UsuarioRepository;

/*
 * "Lembrar de mim" com rotação de refresh token.
 *
 * - O login devolve um refresh token aleatório (30 dias).
 * - Cada renovação invalida o token usado e entrega um novo.
 * - Se um token já usado aparecer de novo, alguém pode ter
 *   copiado a sessão: derrubamos todas as sessões da conta.
 */
@Service
public class RefreshTokenService {

    private static final Logger log =
            LoggerFactory.getLogger(RefreshTokenService.class);

    private static final String SESSAO_INVALIDA =
            "Sessão expirada. Entre novamente.";

    private final RefreshTokenRepository refreshTokenRepository;
    private final UsuarioRepository usuarioRepository;
    private final SecureRandom secureRandom = new SecureRandom();

    @Value("${security.jwt.refresh-expiration-days:30}")
    private long diasDeValidade = 30;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            UsuarioRepository usuarioRepository
    ) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.usuarioRepository = usuarioRepository;
    }

    /*
     * Resultado de uma renovação: dados do dono da sessão
     * (já lidos dentro da transação) e o novo refresh token
     * em texto, que só existe aqui.
     */
    public record Renovacao(String email, String role, String novoToken) {
    }

    @Transactional
    public String emitir(String email) {

        Usuario usuario =
                usuarioRepository
                        .findByEmailIgnoreCase(email)
                        .orElseThrow(() -> new BadCredentialsException(SESSAO_INVALIDA));

        return emitir(usuario);
    }

    /*
     * noRollbackFor: quando recusamos o token, a revogação
     * das sessões precisa ficar gravada mesmo assim.
     */
    @Transactional(noRollbackFor = BadCredentialsException.class)
    public Renovacao renovar(String tokenRecebido) {

        RefreshToken atual =
                refreshTokenRepository
                        .findByTokenHash(hash(tokenRecebido))
                        .orElseThrow(() -> new BadCredentialsException(SESSAO_INVALIDA));

        Usuario usuario = atual.getUsuario();

        if (atual.revogado()) {

            log.warn(
                    "Refresh token reutilizado para usuarioId={}. Encerrando todas as sessões.",
                    usuario.getId()
            );

            revogarTodas(usuario.getId());

            throw new BadCredentialsException(SESSAO_INVALIDA);
        }

        if (atual.expirado()) {
            atual.revogar();
            refreshTokenRepository.save(atual);
            throw new BadCredentialsException(SESSAO_INVALIDA);
        }

        atual.revogar();
        refreshTokenRepository.save(atual);

        return new Renovacao(
                usuario.getEmail(),
                usuario.getRole().name(),
                emitir(usuario)
        );
    }

    /*
     * Logout: derruba só a sessão deste aparelho.
     * Token desconhecido é ignorado (logout nunca falha).
     */
    @Transactional
    public void revogar(String tokenRecebido) {

        if (tokenRecebido == null || tokenRecebido.isBlank()) {
            return;
        }

        refreshTokenRepository
                .findByTokenHash(hash(tokenRecebido))
                .ifPresent(token -> {
                    token.revogar();
                    refreshTokenRepository.save(token);
                });
    }

    /*
     * Troca de senha ou suspeita de roubo:
     * derruba as sessões em todos os aparelhos.
     */
    @Transactional
    public void revogarTodas(Long usuarioId) {

        var ativos =
                refreshTokenRepository
                        .findByUsuarioIdAndRevokedAtIsNull(usuarioId);

        ativos.forEach(RefreshToken::revogar);

        refreshTokenRepository.saveAll(ativos);
    }

    /*
     * Limpeza diária dos tokens vencidos.
     */
    @Scheduled(cron = "0 30 4 * * *", zone = "America/Sao_Paulo")
    @Transactional
    public void limparExpirados() {

        long removidos =
                refreshTokenRepository
                        .deleteByExpiresAtBefore(LocalDateTime.now());

        if (removidos > 0) {
            log.info("{} refresh token(s) vencido(s) removido(s).", removidos);
        }
    }

    private String emitir(Usuario usuario) {

        byte[] bytes = new byte[48];
        secureRandom.nextBytes(bytes);

        String token =
                Base64.getUrlEncoder()
                        .withoutPadding()
                        .encodeToString(bytes);

        LocalDateTime agora = LocalDateTime.now();

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUsuario(usuario);
        refreshToken.setTokenHash(hash(token));
        refreshToken.setCreatedAt(agora);
        refreshToken.setExpiresAt(agora.plusDays(diasDeValidade));

        refreshTokenRepository.save(refreshToken);

        return token;
    }

    private String hash(String token) {

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");

            return HexFormat.of().formatHex(
                    digest.digest(token.getBytes(StandardCharsets.UTF_8))
            );

        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("SHA-256 indisponível.", exception);
        }
    }
}
