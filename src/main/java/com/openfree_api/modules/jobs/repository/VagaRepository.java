package com.openfree_api.modules.jobs.repository;

import com.openfree_api.modules.jobs.entity.StatusVaga;
import com.openfree_api.modules.jobs.entity.Vaga;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VagaRepository extends JpaRepository<Vaga, Long> {

    List<Vaga> findByEmpresaId(Long empresaId);

    List<Vaga> findByStatus(StatusVaga status);

    List<Vaga> findByCidadeIgnoreCase(String cidade);

    List<Vaga> findByEmpresaIdAndStatus(
            Long empresaId,
            StatusVaga status
    );
    long countByEmpresaId(Long empresaId);

long countByEmpresaIdAndStatus(
        Long empresaId,
        StatusVaga status
);


}


