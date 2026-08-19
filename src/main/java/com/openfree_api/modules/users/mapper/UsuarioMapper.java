package com.openfree_api.modules.users.mapper;

import com.openfree_api.modules.users.dto.CreateUsuarioRequest;
import com.openfree_api.modules.users.dto.UsuarioResponse;
import com.openfree_api.modules.users.entity.Usuario;

import org.springframework.stereotype.Component;

@Component
public class UsuarioMapper {

    public Usuario toEntity(
            CreateUsuarioRequest request
    ) {

        Usuario usuario =
                new Usuario();

        usuario.setNome(
                request.getNome()
        );

        usuario.setEmail(
                request.getEmail()
        );

        usuario.setSenha(
                request.getSenha()
        );

        usuario.setTelefone(
                request.getTelefone()
        );

        return usuario;
    }

    public UsuarioResponse toResponse(
            Usuario usuario
    ) {

        UsuarioResponse response =
                new UsuarioResponse();

        response.setId(
                usuario.getId()
        );

        response.setNome(
                usuario.getNome()
        );

        response.setEmail(
                usuario.getEmail()
        );

        response.setTelefone(
                usuario.getTelefone()
        );

        response.setRole(
                usuario.getRole()
        );

        response.setTituloProfissional(
                usuario.getTituloProfissional()
        );

        response.setBiografia(
                usuario.getBiografia()
        );

        response.setCidade(
                usuario.getCidade()
        );

        response.setEstado(
                usuario.getEstado()
        );

        response.setHabilidades(
                usuario.getHabilidades()
        );

        response.setAvatarUrl(
                usuario.getAvatarUrl()
        );

        response.setCurriculoUrl(
                usuario.getCurriculoUrl()
        );

        response.setPortfolioUrl(
                usuario.getPortfolioUrl()
        );

        return response;
    }
}