package com.openfree_api.modules.contracts.mapper;

import com.openfree_api.modules.contracts.dto.ContractResponse;
import com.openfree_api.modules.contracts.entity.Contract;

import org.springframework.stereotype.Component;

@Component
public class ContractMapper {

    public ContractResponse toResponse(
            Contract contract
    ) {

        ContractResponse response =
                new ContractResponse();

        response.setId(
                contract.getId()
        );

        response.setCandidaturaId(
                contract
                        .getCandidatura()
                        .getId()
        );

        response.setVagaId(
                contract
                        .getVaga()
                        .getId()
        );

        response.setVagaTitulo(
                contract
                        .getVaga()
                        .getTitulo()
        );

        response.setEmpresaId(
                contract
                        .getEmpresa()
                        .getId()
        );

        response.setEmpresaNome(
                contract
                        .getEmpresa()
                        .getNomeFantasia()
        );

        response.setFreelancerId(
                contract
                        .getFreelancer()
                        .getId()
        );

        response.setFreelancerNome(
                contract
                        .getFreelancer()
                        .getNome()
        );

        response.setValor(
                contract.getValor()
        );

        response.setStatus(
                contract.getStatus()
        );

        response.setEmpresaConfirmouConclusao(
                contract
                        .getEmpresaConfirmouConclusao()
        );

        response.setFreelancerConfirmouConclusao(
                contract
                        .getFreelancerConfirmouConclusao()
        );

        response.setIniciadoAt(
                contract.getIniciadoAt()
        );

        response.setConcluidoAt(
                contract.getConcluidoAt()
        );

        response.setCreatedAt(
                contract.getCreatedAt()
        );

        response.setUpdatedAt(
                contract.getUpdatedAt()
        );

        return response;
    }
}