package com.openfree_api.modules.favorites.controller;

import com.openfree_api.common.response.ApiResponse;
import com.openfree_api.modules.favorites.dto.FavoriteResponse;
import com.openfree_api.modules.favorites.service.FavoriteService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class FavoriteController {

    private final FavoriteService favoriteService;

    public FavoriteController(
            FavoriteService favoriteService
    ) {
        this.favoriteService = favoriteService;
    }

    @PostMapping("/jobs/{vagaId}/favorite")
    public ResponseEntity<ApiResponse<FavoriteResponse>> favoritar(
            @PathVariable Long vagaId,
            Authentication authentication
    ) {

        FavoriteResponse favorite =
                favoriteService.favoritar(
                        vagaId,
                        authentication
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Vaga adicionada aos favoritos com sucesso.",
                                favorite
                        )
                );
    }

    @DeleteMapping("/jobs/{vagaId}/favorite")
    public ResponseEntity<Void> desfavoritar(
            @PathVariable Long vagaId,
            Authentication authentication
    ) {

        favoriteService.desfavoritar(
                vagaId,
                authentication
        );

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/favorites")
    public ResponseEntity<ApiResponse<List<FavoriteResponse>>> listarFavoritos(
            Authentication authentication
    ) {

        List<FavoriteResponse> favoritos =
                favoriteService.listarFavoritos(
                        authentication
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Vagas favoritas listadas com sucesso.",
                        favoritos
                )
        );
    }
}