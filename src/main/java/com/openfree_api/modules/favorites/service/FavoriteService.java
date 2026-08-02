package com.openfree_api.modules.favorites.service;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.auth.service.UsuarioAuthService;
import com.openfree_api.modules.favorites.dto.FavoriteResponse;
import com.openfree_api.modules.favorites.entity.Favorite;
import com.openfree_api.modules.favorites.mapper.FavoriteMapper;
import com.openfree_api.modules.favorites.repository.FavoriteRepository;
import com.openfree_api.modules.jobs.entity.StatusVaga;
import com.openfree_api.modules.jobs.entity.Vaga;
import com.openfree_api.modules.jobs.repository.VagaRepository;
import com.openfree_api.modules.users.entity.Usuario;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class FavoriteService {

    private static final Logger log =
            LoggerFactory.getLogger(FavoriteService.class);

    private final FavoriteRepository favoriteRepository;
    private final VagaRepository vagaRepository;
    private final FavoriteMapper favoriteMapper;
    private final UsuarioAuthService usuarioAuthService;

    public FavoriteService(
            FavoriteRepository favoriteRepository,
            VagaRepository vagaRepository,
            FavoriteMapper favoriteMapper,
            UsuarioAuthService usuarioAuthService
    ) {
        this.favoriteRepository = favoriteRepository;
        this.vagaRepository = vagaRepository;
        this.favoriteMapper = favoriteMapper;
        this.usuarioAuthService = usuarioAuthService;
    }

    @Transactional
    public FavoriteResponse favoritar(
            Long vagaId,
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService.getUsuarioLogado(authentication);

        Vaga vaga = vagaRepository
                .findById(vagaId)
                .orElseThrow(() -> {

                    log.warn(
                            "Usuário '{}' tentou favoritar uma vaga inexistente. vagaId={}",
                            usuario.getEmail(),
                            vagaId
                    );

                    return new BusinessException(
                            "Vaga não encontrada."
                    );
                });

        if (vaga.getStatus() != StatusVaga.PUBLICADA) {

            log.warn(
                    "Usuário '{}' tentou favoritar a vaga '{}' (id={}) com status '{}'.",
                    usuario.getEmail(),
                    vaga.getTitulo(),
                    vaga.getId(),
                    vaga.getStatus()
            );

            throw new BusinessException(
                    "Somente vagas publicadas podem ser adicionadas aos favoritos."
            );
        }

        if (favoriteRepository.existsByUsuarioIdAndVagaId(
                usuario.getId(),
                vagaId
        )) {

            log.warn(
                    "Usuário '{}' tentou favoritar novamente a vaga '{}' (id={}).",
                    usuario.getEmail(),
                    vaga.getTitulo(),
                    vaga.getId()
            );

            throw new BusinessException(
                    "Esta vaga já está nos seus favoritos."
            );
        }

        Favorite favorite = new Favorite();

        favorite.setUsuario(usuario);
        favorite.setVaga(vaga);

        Favorite favoriteSalvo =
                favoriteRepository.save(favorite);

        log.info(
                "Usuário '{}' favoritou a vaga '{}' (vagaId={}, favoriteId={}).",
                usuario.getEmail(),
                vaga.getTitulo(),
                vaga.getId(),
                favoriteSalvo.getId()
        );

        return favoriteMapper.toResponse(favoriteSalvo);
    }

    @Transactional
    public void desfavoritar(
            Long vagaId,
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService.getUsuarioLogado(authentication);

        Favorite favorite = favoriteRepository
                .findByUsuarioIdAndVagaId(
                        usuario.getId(),
                        vagaId
                )
                .orElseThrow(() -> {

                    log.warn(
                            "Usuário '{}' tentou remover uma vaga que não estava nos favoritos. vagaId={}",
                            usuario.getEmail(),
                            vagaId
                    );

                    return new BusinessException(
                            "Esta vaga não está nos seus favoritos."
                    );
                });

        log.info(
                "Usuário '{}' removeu a vaga '{}' (vagaId={}) dos favoritos.",
                usuario.getEmail(),
                favorite.getVaga().getTitulo(),
                favorite.getVaga().getId()
        );

        favoriteRepository.delete(favorite);
    }

    @Transactional(readOnly = true)
    public List<FavoriteResponse> listarFavoritos(
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService.getUsuarioLogado(authentication);

        List<FavoriteResponse> favoritos =
                favoriteRepository
                        .findByUsuarioIdOrderByCreatedAtDesc(
                                usuario.getId()
                        )
                        .stream()
                        .map(favoriteMapper::toResponse)
                        .toList();

        log.info(
                "Usuário '{}' listou {} vaga(s) favorita(s).",
                usuario.getEmail(),
                favoritos.size()
        );

        return favoritos;
    }
}