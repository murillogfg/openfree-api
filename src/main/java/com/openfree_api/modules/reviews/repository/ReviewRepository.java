package com.openfree_api.modules.reviews.repository;

import com.openfree_api.modules.reviews.entity.Review;
import com.openfree_api.modules.reviews.entity.ReviewAuthorType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ReviewRepository
        extends JpaRepository<Review, Long> {

    boolean existsByCandidaturaIdAndTipoAutor(
            Long candidaturaId,
            ReviewAuthorType tipoAutor
    );

    List<Review> findByUsuarioAvaliadoIdAndTipoAutorOrderByCreatedAtDesc(
            Long usuarioId,
            ReviewAuthorType tipoAutor
    );

    List<Review> findByEmpresaAvaliadaIdAndTipoAutorOrderByCreatedAtDesc(
            Long empresaId,
            ReviewAuthorType tipoAutor
    );

    long countByUsuarioAvaliadoIdAndTipoAutor(
            Long usuarioId,
            ReviewAuthorType tipoAutor
    );

    long countByEmpresaAvaliadaIdAndTipoAutor(
            Long empresaId,
            ReviewAuthorType tipoAutor
    );

    @Query("""
            select avg(r.nota)
            from Review r
            where r.usuarioAvaliado.id = :usuarioId
              and r.tipoAutor = :tipoAutor
            """)
    Double calcularMediaDoUsuario(
            @Param("usuarioId") Long usuarioId,
            @Param("tipoAutor") ReviewAuthorType tipoAutor
    );

    @Query("""
            select avg(r.nota)
            from Review r
            where r.empresaAvaliada.id = :empresaId
              and r.tipoAutor = :tipoAutor
            """)
    Double calcularMediaDaEmpresa(
            @Param("empresaId") Long empresaId,
            @Param("tipoAutor") ReviewAuthorType tipoAutor
    );
}