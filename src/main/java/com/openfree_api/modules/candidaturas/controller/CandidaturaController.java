package com.openfree_api.modules.candidaturas.controller;

import com.openfree_api.common.response.ApiResponse;
import com.openfree_api.modules.candidaturas.dto.CandidaturaResponse;
import com.openfree_api.modules.candidaturas.dto.CreateCandidaturaRequest;
import com.openfree_api.modules.candidaturas.service.CandidaturaService;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.openfree_api.modules.candidaturas.dto.MyApplicationResponse;

import java.util.List;

@RestController
@RequestMapping("/jobs/{vagaId}/applications")
public class CandidaturaController {

    private final CandidaturaService candidaturaService;

    public CandidaturaController(
            CandidaturaService candidaturaService
    ) {
        this.candidaturaService = candidaturaService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<CandidaturaResponse>> criar(
            @PathVariable Long vagaId,
            @Valid @RequestBody CreateCandidaturaRequest request,
            Authentication authentication
    ) {

        CandidaturaResponse candidatura =
                candidaturaService.criar(
                        vagaId,
                        request,
                        authentication
                );

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Candidatura realizada com sucesso.",
                                candidatura
                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CandidaturaResponse>>> listarPorVaga(
            @PathVariable Long vagaId,
            Authentication authentication
    ) {

        List<CandidaturaResponse> candidaturas =
                candidaturaService.listarPorVaga(
                        vagaId,
                        authentication
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Candidaturas da vaga listadas com sucesso.",
                        candidaturas
                )
        );
    }

    @PatchMapping("/{candidaturaId}/accept")
    public ResponseEntity<ApiResponse<CandidaturaResponse>> aceitar(
            @PathVariable Long candidaturaId,
            Authentication authentication
    ) {

        CandidaturaResponse candidatura =
                candidaturaService.aceitar(
                        candidaturaId,
                        authentication
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Candidatura aceita com sucesso.",
                        candidatura
                )
        );
    }

    @PatchMapping("/{candidaturaId}/reject")
    public ResponseEntity<ApiResponse<CandidaturaResponse>> recusar(
            @PathVariable Long candidaturaId,
            Authentication authentication
    ) {

        CandidaturaResponse candidatura =
                candidaturaService.recusar(
                        candidaturaId,
                        authentication
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Candidatura recusada com sucesso.",
                        candidatura
                )
        );
    }



    @GetMapping("/me")
public ResponseEntity<ApiResponse<List<MyApplicationResponse>>>
listarMinhasCandidaturas(
        Authentication authentication
) {

    List<MyApplicationResponse> candidaturas =
            candidaturaService.listarMinhasCandidaturas(
                    authentication
            );

    return ResponseEntity.ok(
            ApiResponse.success(
                    "Minhas candidaturas listadas com sucesso.",
                    candidaturas
            )
    );
}
}