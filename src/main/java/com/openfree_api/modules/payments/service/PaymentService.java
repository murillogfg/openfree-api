package com.openfree_api.modules.payments.service;

import com.openfree_api.common.exception.BusinessException;

import com.openfree_api.modules.auth.service.EmpresaAuthService;
import com.openfree_api.modules.auth.service.UsuarioAuthService;

import com.openfree_api.modules.candidaturas.entity.Candidatura;
import com.openfree_api.modules.candidaturas.entity.StatusCandidatura;

import com.openfree_api.modules.companies.entity.Empresa;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.payments.entity.Payment;
import com.openfree_api.modules.payments.entity.PaymentStatus;
import com.openfree_api.modules.payments.repository.PaymentRepository;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;


import com.openfree_api.modules.payments.dto.PaymentResponse;
import com.openfree_api.modules.payments.entity.Payment;
import com.openfree_api.modules.payments.entity.PaymentMethod;
import com.openfree_api.modules.payments.entity.PaymentStatus;
import com.openfree_api.modules.payments.mapper.PaymentMapper;
import com.openfree_api.modules.payments.repository.PaymentRepository;

import com.openfree_api.modules.users.entity.Usuario;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentMapper paymentMapper;
    private final PaymentFeeService paymentFeeService;
    private final EmpresaAuthService empresaAuthService;
    private final UsuarioAuthService usuarioAuthService;
   


    public PaymentService(
            PaymentRepository paymentRepository,
            PaymentMapper paymentMapper,
            PaymentFeeService paymentFeeService,
            EmpresaAuthService empresaAuthService,
            UsuarioAuthService usuarioAuthService
    ) {
        this.paymentRepository = paymentRepository;
        this.paymentMapper = paymentMapper;
        this.paymentFeeService = paymentFeeService;
        this.empresaAuthService = empresaAuthService;
        this.usuarioAuthService = usuarioAuthService;
    }

    @Transactional
    public PaymentResponse criarPagamentoAutomaticamente(
            Candidatura candidatura
    ) {

        if (
                candidatura.getStatus()
                        != StatusCandidatura.ACEITA
        ) {
            throw new BusinessException(
                    "O pagamento só pode ser criado para candidaturas aceitas."
            );
        }

        return paymentRepository
                .findByCandidaturaId(
                        candidatura.getId()
                )
                .map(paymentMapper::toResponse)
                .orElseGet(() -> {

                    BigDecimal valorBruto;

                    if (
                            candidatura.getValorProposto()
                                    != null
                    ) {
                        valorBruto =
                                candidatura.getValorProposto();
                    } else {
                        valorBruto =
                                candidatura
                                        .getVaga()
                                        .getValor();
                    }

                    if (
                            valorBruto == null
                            || valorBruto.compareTo(
                                    BigDecimal.ZERO
                            ) <= 0
                    ) {
                        throw new BusinessException(
                                "Valor da contratação inválido."
                        );
                    }

                    BigDecimal taxa =
                            paymentFeeService
                                    .calcularTaxa(
                                            valorBruto
                                    );

                    BigDecimal liquido =
                            paymentFeeService
                                    .calcularLiquido(
                                            valorBruto
                                    );

                    Payment payment =
                            new Payment();

                    payment.setCandidatura(
                            candidatura
                    );

                    payment.setEmpresa(
                            candidatura
                                    .getVaga()
                                    .getEmpresa()
                    );

                    payment.setFreelancer(
                            candidatura.getUsuario()
                    );

                    payment.setValorBruto(
                            valorBruto
                    );

                    payment.setTaxaPlataforma(
                            taxa
                    );

                    payment.setValorLiquido(
                            liquido
                    );

                    payment.setStatus(
                            PaymentStatus
                                    .AGUARDANDO_PAGAMENTO
                    );

                    Payment salvo =
                            paymentRepository.save(
                                    payment
                            );

                    return paymentMapper
                            .toResponse(
                                    salvo
                            );
                });
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse>
    listarMeusPagamentos(
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService
                        .getUsuarioLogado(
                                authentication
                        );

        return paymentRepository
                .findByFreelancerIdOrderByCreatedAtDesc(
                        usuario.getId()
                )
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse>
    listarPagamentosDaEmpresa(
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService
                        .getEmpresaLogada(
                                authentication
                        );

        return paymentRepository
                .findByEmpresaIdOrderByCreatedAtDesc(
                        empresa.getId()
                )
                .stream()
                .map(paymentMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PaymentResponse buscarPorId(
            Long paymentId,
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService
                        .getUsuarioLogado(
                                authentication
                        );

        Payment payment =
                paymentRepository
                        .findById(paymentId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Pagamento não encontrado."
                                )
                        );

        boolean freelancer =
                payment
                        .getFreelancer()
                        .getId()
                        .equals(
                                usuario.getId()
                        );

        boolean empresa = false;

        if (!freelancer) {
            try {

                Empresa empresaLogada =
                        empresaAuthService
                                .getEmpresaLogada(
                                        authentication
                                );

                empresa =
                        payment
                                .getEmpresa()
                                .getId()
                                .equals(
                                        empresaLogada
                                                .getId()
                                );

            } catch (BusinessException ignored) {
                // usuário não possui empresa
            }
        }

        if (!freelancer && !empresa) {
            throw new BusinessException(
                    "Você não possui acesso a este pagamento."
            );
        }

        return paymentMapper.toResponse(
                payment
        );
    }

    @Transactional
    public PaymentResponse simularPagamento(
            Long paymentId,
            PaymentMethod metodo,
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService
                        .getEmpresaLogada(
                                authentication
                        );

        Payment payment =
                paymentRepository
                        .findById(paymentId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Pagamento não encontrado."
                                )
                        );

        if (
                !payment
                        .getEmpresa()
                        .getId()
                        .equals(
                                empresa.getId()
                        )
        ) {
            throw new BusinessException(
                    "Este pagamento não pertence à empresa autenticada."
            );
        }

        if (
                payment.getStatus()
                        != PaymentStatus
                        .AGUARDANDO_PAGAMENTO
        ) {
            throw new BusinessException(
                    "Este pagamento não está aguardando pagamento."
            );
        }

        payment.setMetodo(
                metodo
        );

        payment.setStatus(
                PaymentStatus.RETIDO
        );

        payment.setPagoEm(
                LocalDateTime.now()
        );

        payment.setExternalId(
                "SIM-"
                + UUID.randomUUID()
        );

        Payment atualizado =
                paymentRepository.save(
                        payment
                );

        return paymentMapper.toResponse(
                atualizado
        );
    }

    @Transactional
    public PaymentResponse liberarPagamento(
            Long paymentId,
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService
                        .getEmpresaLogada(
                                authentication
                        );

        Payment payment =
                paymentRepository
                        .findById(paymentId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Pagamento não encontrado."
                                )
                        );

        if (
                !payment
                        .getEmpresa()
                        .getId()
                        .equals(
                                empresa.getId()
                        )
        ) {
            throw new BusinessException(
                    "Este pagamento não pertence à empresa autenticada."
            );
        }

        if (
                payment.getStatus()
                        != PaymentStatus.RETIDO
        ) {
            throw new BusinessException(
                    "Somente pagamentos retidos podem ser liberados."
            );
        }

        payment.setStatus(
                PaymentStatus.LIBERADO
        );

        payment.setLiberadoEm(
                LocalDateTime.now()
        );

        Payment atualizado =
                paymentRepository.save(
                        payment
                );

        return paymentMapper.toResponse(
                atualizado
        );
    }

    @Transactional
public void liberarPagamentoAutomaticamente(
        Long candidaturaId
) {

    Payment payment =
            paymentRepository
                    .findByCandidaturaId(
                            candidaturaId
                    )
                    .orElseThrow(() ->
                            new BusinessException(
                                    "Pagamento da contratação não encontrado."
                            )
                    );

    if (
            payment.getStatus()
                    == PaymentStatus.LIBERADO
    ) {
        return;
    }

    if (
            payment.getStatus()
                    != PaymentStatus.RETIDO
    ) {
        throw new BusinessException(
                "O pagamento precisa estar retido antes de ser liberado."
        );
    }

    payment.setStatus(
            PaymentStatus.LIBERADO
    );

    payment.setLiberadoEm(
            LocalDateTime.now()
    );

    paymentRepository.save(
            payment
    );
}
}