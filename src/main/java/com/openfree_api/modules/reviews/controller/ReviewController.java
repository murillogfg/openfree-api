package com.openfree_api.modules.reviews.controller;

import com.openfree_api.common.response.ApiResponse;
import com.openfree_api.modules.reviews.dto.CreateReviewRequest;
import com.openfree_api.modules.reviews.dto.RatingSummaryResponse;
import com.openfree_api.modules.reviews.dto.ReviewResponse;
import com.openfree_api.modules.reviews.service.ReviewService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    public ReviewController(
            ReviewService reviewService
    ) {
        this.reviewService = reviewService;
    }

    @PostMapping("/applications/{candidaturaId}/freelancer")
    public ResponseEntity<ApiResponse<ReviewResponse>> avaliarFreelancer(
            @PathVariable Long candidaturaId,
            @Valid @RequestBody CreateReviewRequest request,
            Authentication authentication
    ) {

        ReviewResponse review =
                reviewService.avaliarFreelancer(
                        candidaturaId,
                        request,
                        authentication
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Freelancer avaliado com sucesso.",
                                review
                        )
                );
    }

    @PostMapping("/applications/{candidaturaId}/company")
    public ResponseEntity<ApiResponse<ReviewResponse>> avaliarEmpresa(
            @PathVariable Long candidaturaId,
            @Valid @RequestBody CreateReviewRequest request,
            Authentication authentication
    ) {

        ReviewResponse review =
                reviewService.avaliarEmpresa(
                        candidaturaId,
                        request,
                        authentication
                );

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Empresa avaliada com sucesso.",
                                review
                        )
                );
    }

    @GetMapping("/users/{usuarioId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> listarAvaliacoesDoUsuario(
            @PathVariable Long usuarioId
    ) {

        List<ReviewResponse> reviews =
                reviewService.listarAvaliacoesDoUsuario(
                        usuarioId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Avaliações do usuário listadas com sucesso.",
                        reviews
                )
        );
    }

    @GetMapping("/companies/{empresaId}")
    public ResponseEntity<ApiResponse<List<ReviewResponse>>> listarAvaliacoesDaEmpresa(
            @PathVariable Long empresaId
    ) {

        List<ReviewResponse> reviews =
                reviewService.listarAvaliacoesDaEmpresa(
                        empresaId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Avaliações da empresa listadas com sucesso.",
                        reviews
                )
        );
    }

    @GetMapping("/users/{usuarioId}/summary")
    public ResponseEntity<ApiResponse<RatingSummaryResponse>> resumoDoUsuario(
            @PathVariable Long usuarioId
    ) {

        RatingSummaryResponse resumo =
                reviewService.resumoDoUsuario(
                        usuarioId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Resumo das avaliações do usuário carregado com sucesso.",
                        resumo
                )
        );
    }

    @GetMapping("/companies/{empresaId}/summary")
    public ResponseEntity<ApiResponse<RatingSummaryResponse>> resumoDaEmpresa(
            @PathVariable Long empresaId
    ) {

        RatingSummaryResponse resumo =
                reviewService.resumoDaEmpresa(
                        empresaId
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Resumo das avaliações da empresa carregado com sucesso.",
                        resumo
                )
        );
    }
}