package com.openfree_api.modules.favorites.repository;

import com.openfree_api.modules.favorites.entity.Favorite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FavoriteRepository
        extends JpaRepository<Favorite, Long> {

    boolean existsByUsuarioIdAndVagaId(
            Long usuarioId,
            Long vagaId
    );

    Optional<Favorite> findByUsuarioIdAndVagaId(
            Long usuarioId,
            Long vagaId
    );

    List<Favorite> findByUsuarioIdOrderByCreatedAtDesc(
            Long usuarioId
    );
}