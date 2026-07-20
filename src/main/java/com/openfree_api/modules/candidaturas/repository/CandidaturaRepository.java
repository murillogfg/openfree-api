package com.openfree_api.modules.candidaturas.repository;

import com.openfree_api.modules.candidaturas.entity.Candidatura;
import com.openfree_api.modules.candidaturas.entity.StatusCandidatura;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CandidaturaRepository
        extends JpaRepository<Candidatura, Long> {

    List<Candidatura> findByVagaId(Long vagaId);

    List<Candidatura> findByUsuarioId(Long usuarioId);

    List<Candidatura> findByStatus(StatusCandidatura status);

    Optional<Candidatura> findByVagaIdAndUsuarioId(
            Long vagaId,
            Long usuarioId
    );

    boolean existsByVagaIdAndUsuarioId(
            Long vagaId,
            Long usuarioId
    );

    long countByVagaIdAndStatus(
        Long vagaId,
        StatusCandidatura status
);

    List<Candidatura> findByVagaIdAndStatusIn(
        Long vagaId,
        List<StatusCandidatura> status
);

long countByVagaEmpresaId(Long empresaId);

long countByVagaEmpresaIdAndStatus(
        Long empresaId,
        StatusCandidatura status
);





}