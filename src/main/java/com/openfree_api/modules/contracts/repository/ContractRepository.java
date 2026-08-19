package com.openfree_api.modules.contracts.repository;

import com.openfree_api.modules.contracts.entity.Contract;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ContractRepository
        extends JpaRepository<Contract, Long> {

    boolean existsByCandidaturaId(
            Long candidaturaId
    );

    Optional<Contract> findByCandidaturaId(
            Long candidaturaId
    );

    List<Contract> findByFreelancerIdOrderByCreatedAtDesc(
            Long freelancerId
    );

    List<Contract> findByEmpresaIdOrderByCreatedAtDesc(
            Long empresaId
    );
}