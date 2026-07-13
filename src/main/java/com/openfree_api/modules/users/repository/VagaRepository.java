package com.openfree_api.modules.users.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.openfree_api.modules.users.model.Vaga;

@Repository
public interface VagaRepository extends JpaRepository<Vaga, Long> {
}


