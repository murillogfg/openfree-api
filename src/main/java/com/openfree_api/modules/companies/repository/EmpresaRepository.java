package com.openfree_api.modules.companies.repository;

import com.openfree_api.modules.companies.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface EmpresaRepository extends JpaRepository<Empresa, Long> {

    Optional<Empresa> findByCnpj(String cnpj);

    Optional<Empresa> findByEmail(String email);

    boolean existsByCnpj(String cnpj);

    boolean existsByEmail(String email);
}


