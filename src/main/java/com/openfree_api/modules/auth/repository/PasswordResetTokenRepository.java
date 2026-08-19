package com.openfree_api.modules.auth.repository;

import com.openfree_api.modules.auth.entity.PasswordResetToken;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PasswordResetTokenRepository
        extends JpaRepository<
            PasswordResetToken,
            Long
        > {

    Optional<PasswordResetToken>
    findByTokenHash(
            String tokenHash
    );
}