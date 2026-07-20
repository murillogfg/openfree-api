package com.openfree_api.modules.candidaturas.mapper;

import com.openfree_api.modules.candidaturas.dto.CandidaturaResponse;
import com.openfree_api.modules.candidaturas.dto.CreateCandidaturaRequest;
import com.openfree_api.modules.candidaturas.entity.Candidatura;
import org.springframework.stereotype.Component;

@Component
public class CandidaturaMapper {

    public Candidatura toEntity(CreateCandidaturaRequest request) {

        Candidatura candidatura = new Candidatura();

        candidatura.setMensagem(request.getMensagem());
        candidatura.setValorProposto(request.getValorProposto());

        return candidatura;
    }

    public CandidaturaResponse toResponse(Candidatura candidatura) {

        CandidaturaResponse response = new CandidaturaResponse();

        response.setId(candidatura.getId());

        response.setUsuarioId(
                candidatura.getUsuario().getId()
        );

        response.setNome(
                candidatura.getUsuario().getNome()
        );

        response.setEmail(
                candidatura.getUsuario().getEmail()
        );

        response.setVagaId(
                candidatura.getVaga().getId()
        );

        response.setVagaTitulo(
                candidatura.getVaga().getTitulo()
        );

        response.setMensagem(candidatura.getMensagem());
        response.setValorProposto(candidatura.getValorProposto());
        response.setStatus(candidatura.getStatus());
        response.setEmpresaVisualizou(candidatura.getEmpresaVisualizou());
        response.setCreatedAt(candidatura.getCreatedAt());
        response.setCreatedAt(candidatura.getUpdatedAt());

        return response;
    }
}