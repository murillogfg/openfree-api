package com.openfree_api.modules.payments.repository;

import com.openfree_api.modules.payments.entity.Payment;
import com.openfree_api.modules.payments.entity.PaymentStatus;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

import java.util.Optional;



public interface PaymentRepository
        extends JpaRepository<Payment, Long> {

    Optional<Payment> findByCandidaturaId(
            Long candidaturaId
    );

    boolean existsByCandidaturaId(
            Long candidaturaId
    );

    List<Payment> findByEmpresaIdOrderByCreatedAtDesc(
            Long empresaId
    );

    List<Payment> findByFreelancerIdOrderByCreatedAtDesc(
            Long freelancerId
    );

    long countByEmpresaIdAndStatus(
            Long empresaId,
            PaymentStatus status
    );

    long countByFreelancerIdAndStatus(
            Long freelancerId,
            PaymentStatus status
    );


}