package com.openfree_api.modules.auth.service;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.auth.entity.PasswordResetToken;
import com.openfree_api.modules.auth.repository.PasswordResetTokenRepository;
import com.openfree_api.modules.users.entity.Usuario;
import com.openfree_api.modules.users.repository.UsuarioRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import java.time.LocalDateTime;

import java.util.Base64;
import java.util.HexFormat;

@Service
public class PasswordResetService {

    private static final Logger log =
            LoggerFactory.getLogger(
                    PasswordResetService.class
            );

    private static final int TOKEN_EXPIRATION_MINUTES =
            30;

    private final UsuarioRepository usuarioRepository;

    private final PasswordResetTokenRepository
            passwordResetTokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom =
            new SecureRandom();

    public PasswordResetService(
            UsuarioRepository usuarioRepository,
            PasswordResetTokenRepository
                    passwordResetTokenRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.usuarioRepository =
                usuarioRepository;

        this.passwordResetTokenRepository =
                passwordResetTokenRepository;

        this.passwordEncoder =
                passwordEncoder;
    }

    @Transactional
    public void solicitarRecuperacao(
            String email
    ) {

        String emailNormalizado =
                email
                        .trim()
                        .toLowerCase();

        Usuario usuario =
                usuarioRepository
                        .findByEmail(emailNormalizado)
                        .orElse(null);

        /*
         * Não informamos ao cliente se o e-mail existe.
         *
         * Isso evita enumeração de contas.
         */
        if (usuario == null) {

            log.info(
                    "Solicitação de recuperação recebida para e-mail não cadastrado."
            );

            return;
        }

        String token =
                gerarToken();

        String hash =
                gerarHash(token);

        PasswordResetToken resetToken =
                new PasswordResetToken();

        resetToken.setUsuario(
                usuario
        );

        resetToken.setTokenHash(
                hash
        );

        resetToken.setExpiresAt(
                LocalDateTime.now()
                        .plusMinutes(
                                TOKEN_EXPIRATION_MINUTES
                        )
        );

        passwordResetTokenRepository.save(
                resetToken
        );

        /*
         * APENAS DESENVOLVIMENTO LOCAL.
         *
         * Depois substituiremos isto por envio real
         * de e-mail.
         */
        log.info(
                "DEV ONLY - Reset password URL: http://localhost:4200/reset-password?token={}",
                token
        );
    }

    @Transactional
    public void redefinirSenha(
            String token,
            String novaSenha
    ) {

        String hash =
                gerarHash(token);

        PasswordResetToken resetToken =
                passwordResetTokenRepository
                        .findByTokenHash(hash)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Token de recuperação inválido."
                                )
                        );

        if (resetToken.utilizado()) {

            throw new BusinessException(
                    "Este token de recuperação já foi utilizado."
            );
        }

        if (resetToken.expirado()) {

            throw new BusinessException(
                    "Este token de recuperação expirou."
            );
        }

        Usuario usuario =
                resetToken.getUsuario();

        String senhaCriptografada =
                passwordEncoder.encode(
                        novaSenha
                );

        usuario.setSenha(
                senhaCriptografada
        );

        usuarioRepository.save(
                usuario
        );

        resetToken.utilizar();

        passwordResetTokenRepository.save(
                resetToken
        );

        log.info(
                "Senha redefinida com sucesso para usuarioId={}",
                usuario.getId()
        );
    }

    private String gerarToken() {

        byte[] bytes =
                new byte[32];

        secureRandom.nextBytes(
                bytes
        );

        return Base64
                .getUrlEncoder()
                .withoutPadding()
                .encodeToString(
                        bytes
                );
    }

    private String gerarHash(
            String token
    ) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance(
                            "SHA-256"
                    );

            byte[] hash =
                    digest.digest(
                            token.getBytes(
                                    StandardCharsets.UTF_8
                            )
                    );

            return HexFormat
                    .of()
                    .formatHex(hash);

        } catch (
                NoSuchAlgorithmException exception
        ) {

            throw new IllegalStateException(
                    "Não foi possível gerar o hash do token.",
                    exception
            );
        }
    }
}