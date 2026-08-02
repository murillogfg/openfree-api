package com.openfree_api.modules.favorites.mapper;

import com.openfree_api.modules.favorites.dto.FavoriteResponse;
import com.openfree_api.modules.favorites.entity.Favorite;
import com.openfree_api.modules.jobs.entity.Vaga;
import org.springframework.stereotype.Component;

@Component
public class FavoriteMapper {

    public FavoriteResponse toResponse(Favorite favorite) {

        FavoriteResponse response =
                new FavoriteResponse();

        Vaga vaga = favorite.getVaga();

        response.setId(favorite.getId());
        response.setVagaId(vaga.getId());
        response.setTitulo(vaga.getTitulo());

        response.setEmpresaNome(
                vaga.getEmpresa().getNomeFantasia()
        );

        response.setCidade(vaga.getCidade());
        response.setEstado(vaga.getEstado());
        response.setValor(vaga.getValor());
        response.setDataServico(vaga.getDataServico());
        response.setStatus(vaga.getStatus());
        response.setFavoritadoEm(favorite.getCreatedAt());

        return response;
    }
}