package com.openfree_api.modules.jobs.repository;

import com.openfree_api.modules.jobs.entity.StatusVaga;
import com.openfree_api.modules.jobs.entity.Vaga;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;


public interface VagaRepository extends
        JpaRepository<Vaga, Long>,
        JpaSpecificationExecutor<Vaga> {

    List<Vaga> findByEmpresaId(
            Long empresaId
    );

    List<Vaga> findByStatus(
            StatusVaga status
    );

    List<Vaga> findByCidadeIgnoreCase(
            String cidade
    );

    List<Vaga> findByEmpresaIdAndStatus(
            Long empresaId,
            StatusVaga status
    );

    Optional<Vaga> findByIdAndEmpresaId(
            Long vagaId,
            Long empresaId
    );

    Optional<Vaga> findByIdAndStatus(
            Long vagaId,
            StatusVaga status
    );

    long countByEmpresaId(
            Long empresaId
    );

    long countByEmpresaIdAndStatus(
            Long empresaId,
            StatusVaga status
    );
}