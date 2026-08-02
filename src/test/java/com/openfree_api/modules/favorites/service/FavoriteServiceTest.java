package com.openfree_api.modules.favorites.service;

import com.openfree_api.modules.auth.service.UsuarioAuthService;
import com.openfree_api.modules.favorites.dto.FavoriteResponse;
import com.openfree_api.modules.favorites.entity.Favorite;
import com.openfree_api.modules.favorites.mapper.FavoriteMapper;
import com.openfree_api.modules.favorites.repository.FavoriteRepository;
import com.openfree_api.modules.jobs.entity.StatusVaga;
import com.openfree_api.modules.jobs.entity.Vaga;
import com.openfree_api.modules.jobs.repository.VagaRepository;
import com.openfree_api.modules.users.entity.Usuario;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openfree_api.common.exception.BusinessException;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.Mockito.never;




@ExtendWith(MockitoExtension.class)
class FavoriteServiceTest {

    @Mock
    private FavoriteRepository favoriteRepository;

    @Mock
    private VagaRepository vagaRepository;

    @Mock
    private FavoriteMapper favoriteMapper;

    @Mock
    private UsuarioAuthService usuarioAuthService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private FavoriteService favoriteService;

    @Test
    void deveFavoritarVagaComSucesso() {

        Long vagaId = 10L;

        Usuario usuario = new Usuario();
        usuario.setId(20L);
        usuario.setNome("Murillo");
        usuario.setEmail("murillo@email.com");

        Vaga vaga = new Vaga();
        vaga.setId(vagaId);
        vaga.setTitulo("Cozinheiro para evento");
        vaga.setStatus(StatusVaga.PUBLICADA);

        FavoriteResponse responseEsperada =
                new FavoriteResponse();

        responseEsperada.setVagaId(vagaId);
        responseEsperada.setTitulo(
                "Cozinheiro para evento"
        );

        when(
                usuarioAuthService.getUsuarioLogado(
                        authentication
                )
        ).thenReturn(usuario);

        when(
                vagaRepository.findById(vagaId)
        ).thenReturn(
                Optional.of(vaga)
        );

        when(
                favoriteRepository.existsByUsuarioIdAndVagaId(
                        usuario.getId(),
                        vagaId
                )
        ).thenReturn(false);

        when(
                favoriteRepository.save(
                        any(Favorite.class)
                )
        ).thenAnswer(
                invocation -> invocation.getArgument(0)
        );

        when(
                favoriteMapper.toResponse(
                        any(Favorite.class)
                )
        ).thenReturn(responseEsperada);

        FavoriteResponse response =
                favoriteService.favoritar(
                        vagaId,
                        authentication
                );

        assertNotNull(response);

        assertEquals(
                vagaId,
                response.getVagaId()
        );

        assertEquals(
                "Cozinheiro para evento",
                response.getTitulo()
        );

        ArgumentCaptor<Favorite> captor =
                ArgumentCaptor.forClass(
                        Favorite.class
                );

        verify(favoriteRepository)
                .save(captor.capture());

        Favorite favoriteSalvo =
                captor.getValue();

        assertSame(
                usuario,
                favoriteSalvo.getUsuario()
        );

        assertSame(
                vaga,
                favoriteSalvo.getVaga()
        );

        verify(
                usuarioAuthService
        ).getUsuarioLogado(authentication);

        verify(
                vagaRepository
        ).findById(vagaId);

        verify(
                favoriteRepository
        ).existsByUsuarioIdAndVagaId(
                usuario.getId(),
                vagaId
        );

        verify(
                favoriteMapper
        ).toResponse(any(Favorite.class));
    }


@Test
void deveImpedirFavoritarDuasVezes() {

    Long vagaId = 10L;

    Usuario usuario = new Usuario();
    usuario.setId(20L);
    usuario.setEmail("murillo@email.com");

    Vaga vaga = new Vaga();
    vaga.setId(vagaId);
    vaga.setTitulo("Cozinheiro");
    vaga.setStatus(StatusVaga.PUBLICADA);

    when(
            usuarioAuthService.getUsuarioLogado(authentication)
    ).thenReturn(usuario);

    when(
            vagaRepository.findById(vagaId)
    ).thenReturn(Optional.of(vaga));

    when(
            favoriteRepository.existsByUsuarioIdAndVagaId(
                    usuario.getId(),
                    vagaId
            )
    ).thenReturn(true);

    BusinessException exception =
            assertThrows(
                    BusinessException.class,
                    () -> favoriteService.favoritar(
                            vagaId,
                            authentication
                    )
            );

    assertEquals(
            "Esta vaga já está nos seus favoritos.",
            exception.getMessage()
    );

    verify(
            favoriteRepository,
            never()
    ).save(any(Favorite.class));
}

    @Test
void deveListarFavoritosDoUsuario() {

    Usuario usuario = new Usuario();
    usuario.setId(20L);
    usuario.setEmail("murillo@email.com");

    Vaga vaga = new Vaga();
    vaga.setId(10L);
    vaga.setTitulo("Cozinheiro");

    Favorite favorite = new Favorite();
    favorite.setUsuario(usuario);
    favorite.setVaga(vaga);

    FavoriteResponse response =
            new FavoriteResponse();

    response.setVagaId(10L);
    response.setTitulo("Cozinheiro");

    when(
            usuarioAuthService.getUsuarioLogado(authentication)
    ).thenReturn(usuario);

    when(
            favoriteRepository.findByUsuarioIdOrderByCreatedAtDesc(
                    usuario.getId()
            )
    ).thenReturn(List.of(favorite));

    when(
            favoriteMapper.toResponse(favorite)
    ).thenReturn(response);

    List<FavoriteResponse> favoritos =
            favoriteService.listarFavoritos(
                    authentication
            );

    assertEquals(
            1,
            favoritos.size()
    );

    assertEquals(
            "Cozinheiro",
            favoritos.get(0).getTitulo()
    );

    verify(
            favoriteRepository
    ).findByUsuarioIdOrderByCreatedAtDesc(
            usuario.getId()
    );
}


@Test
void deveRemoverFavoritoComSucesso() {

    Long vagaId = 10L;

    Usuario usuario = new Usuario();
    usuario.setId(20L);
    usuario.setEmail("murillo@email.com");

    Vaga vaga = new Vaga();
    vaga.setId(vagaId);
    vaga.setTitulo("Cozinheiro");

    Favorite favorite = new Favorite();
    favorite.setUsuario(usuario);
    favorite.setVaga(vaga);

    when(
            usuarioAuthService.getUsuarioLogado(authentication)
    ).thenReturn(usuario);

    when(
            favoriteRepository.findByUsuarioIdAndVagaId(
                    usuario.getId(),
                    vagaId
            )
    ).thenReturn(Optional.of(favorite));

    favoriteService.desfavoritar(
            vagaId,
            authentication
    );

    verify(
            favoriteRepository
    ).delete(favorite);
}


}