package com.openfree_api.modules.auth.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.openfree_api.modules.auth.entity.RefreshToken;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {

    Optional<RefreshToken> findByTokenHash(String tokenHash);

    List<RefreshToken> findByUsuarioIdAndRevokedAtIsNull(Long usuarioId);

    long deleteByExpiresAtBefore(LocalDateTime limite);
}
