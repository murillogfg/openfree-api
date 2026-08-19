package com.openfree_api.modules.payments.mapper;

import com.openfree_api.modules.payments.dto.PaymentResponse;
import com.openfree_api.modules.payments.entity.Payment;

import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(
            Payment payment
    ) {

        PaymentResponse response =
                new PaymentResponse();

        response.setId(
                payment.getId()
        );

        response.setCandidaturaId(
                payment.getCandidatura().getId()
        );

        response.setVagaId(
                payment
                        .getCandidatura()
                        .getVaga()
                        .getId()
        );

        response.setVagaTitulo(
                payment
                        .getCandidatura()
                        .getVaga()
                        .getTitulo()
        );

        response.setEmpresaId(
                payment.getEmpresa().getId()
        );

        response.setEmpresaNome(
                payment
                        .getEmpresa()
                        .getNomeFantasia()
        );

        response.setFreelancerId(
                payment.getFreelancer().getId()
        );

        response.setFreelancerNome(
                payment
                        .getFreelancer()
                        .getNome()
        );

        response.setValorBruto(
                payment.getValorBruto()
        );

        response.setTaxaPlataforma(
                payment.getTaxaPlataforma()
        );

        response.setValorLiquido(
                payment.getValorLiquido()
        );

        response.setStatus(
                payment.getStatus()
        );

        response.setMetodo(
                payment.getMetodo()
        );

        response.setExternalId(
                payment.getExternalId()
        );

        response.setPagoEm(
                payment.getPagoEm()
        );

        response.setLiberadoEm(
                payment.getLiberadoEm()
        );

        response.setCreatedAt(
                payment.getCreatedAt()
        );

        response.setUpdatedAt(
                payment.getUpdatedAt()
        );

        return response;
    }
}