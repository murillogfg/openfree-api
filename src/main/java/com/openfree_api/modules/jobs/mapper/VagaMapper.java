package com.openfree_api.modules.jobs.mapper;

import com.openfree_api.modules.jobs.dto.CreateVagaRequest;
import com.openfree_api.modules.jobs.dto.VagaResponse;
import com.openfree_api.modules.jobs.entity.Vaga;
import org.springframework.stereotype.Component;

@Component
public class VagaMapper {

    public Vaga toEntity(CreateVagaRequest request) {

        Vaga vaga = new Vaga();

        vaga.setTitulo(request.getTitulo());
        vaga.setDescricao(request.getDescricao());
        vaga.setRequisitos(request.getRequisitos());
        vaga.setCidade(request.getCidade());
        vaga.setEstado(request.getEstado());
        vaga.setValor(request.getValor());
        vaga.setQuantidadePessoas(request.getQuantidadePessoas());
        vaga.setDataServico(request.getDataServico());
        vaga.setHorarioInicio(request.getHorarioInicio());
        vaga.setHorarioFim(request.getHorarioFim());

        return vaga;
    }

    public VagaResponse toResponse(Vaga vaga) {

        VagaResponse response = new VagaResponse();

        response.setId(vaga.getId());

        response.setEmpresaId(
                vaga.getEmpresa().getId()
        );

        response.setEmpresaNome(
                vaga.getEmpresa().getNomeFantasia()
        );

        response.setTitulo(vaga.getTitulo());
        response.setDescricao(vaga.getDescricao());
        response.setRequisitos(vaga.getRequisitos());
        response.setCidade(vaga.getCidade());
        response.setEstado(vaga.getEstado());
        response.setValor(vaga.getValor());
        response.setQuantidadePessoas(vaga.getQuantidadePessoas());
        response.setDataServico(vaga.getDataServico());
        response.setHorarioInicio(vaga.getHorarioInicio());
        response.setHorarioFim(vaga.getHorarioFim());
        response.setStatus(vaga.getStatus());
        response.setCreatedAt(vaga.getCreatedAt());
        response.setUpdatedAt(vaga.getUpdatedAt());

        return response;
    }
}