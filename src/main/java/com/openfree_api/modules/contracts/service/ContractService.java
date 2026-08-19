package com.openfree_api.modules.contracts.service;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.auth.service.EmpresaAuthService;
import com.openfree_api.modules.auth.service.UsuarioAuthService;
import com.openfree_api.modules.candidaturas.entity.Candidatura;
import com.openfree_api.modules.candidaturas.entity.StatusCandidatura;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.contracts.dto.ContractResponse;
import com.openfree_api.modules.contracts.entity.Contract;
import com.openfree_api.modules.contracts.entity.ContractStatus;
import com.openfree_api.modules.contracts.mapper.ContractMapper;
import com.openfree_api.modules.contracts.repository.ContractRepository;
import com.openfree_api.modules.payments.service.PaymentService;
import com.openfree_api.modules.users.entity.Usuario;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class ContractService {

    private final ContractRepository contractRepository;
    private final EmpresaAuthService empresaAuthService;
    private final UsuarioAuthService usuarioAuthService;
    private final ContractMapper contractMapper;
    private final PaymentService paymentService;

    public ContractService(
            ContractRepository contractRepository,
            EmpresaAuthService empresaAuthService,
            UsuarioAuthService usuarioAuthService,
            ContractMapper contractMapper,
            PaymentService paymentService
    ) {
        this.contractRepository = contractRepository;
        this.empresaAuthService = empresaAuthService;
        this.usuarioAuthService = usuarioAuthService;
        this.contractMapper = contractMapper;
        this.paymentService = paymentService;
    }

    @Transactional
    public Contract criarContratoAutomaticamente(
            Candidatura candidatura
    ) {

        if (
                candidatura.getStatus()
                        != StatusCandidatura.ACEITA
        ) {
            throw new BusinessException(
                    "O contrato só pode ser criado para candidaturas aceitas."
            );
        }

        return contractRepository
                .findByCandidaturaId(
                        candidatura.getId()
                )
                .orElseGet(() -> {

                    BigDecimal valor =
                            candidatura.getValorProposto();

                    if (valor == null) {
                        valor =
                                candidatura
                                        .getVaga()
                                        .getValor();
                    }

                    if (
                            valor == null
                            || valor.compareTo(
                                    BigDecimal.ZERO
                            ) <= 0
                    ) {
                        throw new BusinessException(
                                "O valor da contratação é inválido."
                        );
                    }

                    Contract contract =
                            new Contract();

                    contract.setCandidatura(
                            candidatura
                    );

                    contract.setVaga(
                            candidatura.getVaga()
                    );

                    contract.setEmpresa(
                            candidatura
                                    .getVaga()
                                    .getEmpresa()
                    );

                    contract.setFreelancer(
                            candidatura.getUsuario()
                    );

                    contract.setValor(
                            valor
                    );

                    contract.setStatus(
                            ContractStatus.AGUARDANDO_INICIO
                    );

                    return contractRepository.save(
                            contract
                    );
                });
    }

    @Transactional(readOnly = true)
    public List<ContractResponse> listarMeusContratos(
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService
                        .getUsuarioLogado(
                                authentication
                        );

        return contractRepository
                .findByFreelancerIdOrderByCreatedAtDesc(
                        usuario.getId()
                )
                .stream()
                .map(contractMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ContractResponse> listarContratosDaEmpresa(
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService
                        .getEmpresaLogada(
                                authentication
                        );

        return contractRepository
                .findByEmpresaIdOrderByCreatedAtDesc(
                        empresa.getId()
                )
                .stream()
                .map(contractMapper::toResponse)
                .toList();
    }

    @Transactional
    public ContractResponse iniciarContrato(
            Long contractId,
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService
                        .getEmpresaLogada(
                                authentication
                        );

        Contract contract =
                contractRepository
                        .findById(contractId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Contrato não encontrado."
                                )
                        );

        if (
                !contract
                        .getEmpresa()
                        .getId()
                        .equals(
                                empresa.getId()
                        )
        ) {
            throw new BusinessException(
                    "Este contrato não pertence à empresa autenticada."
            );
        }

        if (
                contract.getStatus()
                        != ContractStatus.AGUARDANDO_INICIO
        ) {
            throw new BusinessException(
                    "Este contrato não pode ser iniciado."
            );
        }

        contract.setStatus(
                ContractStatus.EM_ANDAMENTO
        );

        contract.setIniciadoAt(
                LocalDateTime.now()
        );

        Contract atualizado =
                contractRepository.save(
                        contract
                );

        return contractMapper.toResponse(
                atualizado
        );
    }

    @Transactional
    public ContractResponse confirmarConclusaoEmpresa(
            Long contractId,
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService
                        .getEmpresaLogada(
                                authentication
                        );

        Contract contract =
                contractRepository
                        .findById(contractId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Contrato não encontrado."
                                )
                        );

        if (
                !contract
                        .getEmpresa()
                        .getId()
                        .equals(
                                empresa.getId()
                        )
        ) {
            throw new BusinessException(
                    "Este contrato não pertence à empresa autenticada."
            );
        }

        if (
                contract.getStatus()
                        != ContractStatus.EM_ANDAMENTO
                &&
                contract.getStatus()
                        != ContractStatus.AGUARDANDO_CONFIRMACAO
        ) {
            throw new BusinessException(
                    "Este contrato não pode ser concluído neste momento."
            );
        }

        contract.setEmpresaConfirmouConclusao(
                true
        );

        atualizarStatusConclusao(
                contract
        );

        Contract atualizado =
                contractRepository.save(
                        contract
                );

        return contractMapper.toResponse(
                atualizado
        );
    }

    @Transactional
    public ContractResponse confirmarConclusaoFreelancer(
            Long contractId,
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService
                        .getUsuarioLogado(
                                authentication
                        );

        Contract contract =
                contractRepository
                        .findById(contractId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Contrato não encontrado."
                                )
                        );

        if (
                !contract
                        .getFreelancer()
                        .getId()
                        .equals(
                                usuario.getId()
                        )
        ) {
            throw new BusinessException(
                    "Este contrato não pertence ao freelancer autenticado."
            );
        }

        if (
                contract.getStatus()
                        != ContractStatus.EM_ANDAMENTO
                &&
                contract.getStatus()
                        != ContractStatus.AGUARDANDO_CONFIRMACAO
        ) {
            throw new BusinessException(
                    "Este contrato não pode ser concluído neste momento."
            );
        }

        contract.setFreelancerConfirmouConclusao(
                true
        );

        atualizarStatusConclusao(
                contract
        );

        Contract atualizado =
                contractRepository.save(
                        contract
                );

        return contractMapper.toResponse(
                atualizado
        );
    }

    private void atualizarStatusConclusao(
            Contract contract
    ) {

        boolean empresaConfirmou =
                Boolean.TRUE.equals(
                        contract
                                .getEmpresaConfirmouConclusao()
                );

        boolean freelancerConfirmou =
                Boolean.TRUE.equals(
                        contract
                                .getFreelancerConfirmouConclusao()
                );

        if (
                empresaConfirmou
                && freelancerConfirmou
        ) {

            contract.setStatus(
                    ContractStatus.CONCLUIDO
            );

            contract.setConcluidoAt(
                    LocalDateTime.now()
            );

            paymentService
                    .liberarPagamentoAutomaticamente(
                            contract
                                    .getCandidatura()
                                    .getId()
                    );

            return;
        }

        if (
                empresaConfirmou
                || freelancerConfirmou
        ) {
            contract.setStatus(
                    ContractStatus.AGUARDANDO_CONFIRMACAO
            );
        }
    }

    @Transactional(readOnly = true)
    public ContractResponse buscarPorId(
            Long contractId,
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService
                        .getUsuarioLogado(
                                authentication
                        );

        Contract contract =
                contractRepository
                        .findById(contractId)
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Contrato não encontrado."
                                )
                        );

        boolean freelancer =
                contract
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
                        contract
                                .getEmpresa()
                                .getId()
                                .equals(
                                        empresaLogada
                                                .getId()
                                );

            } catch (BusinessException ignored) {
                // Usuário autenticado não possui empresa.
            }
        }

        if (!freelancer && !empresa) {

            throw new BusinessException(
                    "Você não possui acesso a este contrato."
            );
        }

        return contractMapper.toResponse(
                contract
        );
    }
}