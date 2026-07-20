package com.openfree_api.modules.candidaturas.controller;

import com.openfree_api.common.response.ApiResponse;
import com.openfree_api.modules.candidaturas.dto.CandidaturaResponse;
import com.openfree_api.modules.candidaturas.dto.CreateCandidaturaRequest;
import com.openfree_api.modules.candidaturas.service.CandidaturaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
            @Valid @RequestBody CreateCandidaturaRequest request
    ) {

        CandidaturaResponse candidatura =
                candidaturaService.criar(vagaId, request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Candidatura realizada com sucesso.",
                                candidatura
                        )
                );
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<CandidaturaResponse>>> listarPorVaga(
            @PathVariable Long vagaId
    ) {

        List<CandidaturaResponse> candidaturas =
                candidaturaService.listarPorVaga(vagaId);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Candidaturas da vaga listadas com sucesso.",
                        candidaturas
                )
        );
    }

    @PatchMapping("/{candidaturaId}/accept")
public ResponseEntity<ApiResponse<CandidaturaResponse>> aceitar(
        @PathVariable Long candidaturaId
) {

    CandidaturaResponse candidatura =
            candidaturaService.aceitar(candidaturaId);

    return ResponseEntity.ok(
            ApiResponse.success(
                    "Candidatura aceita com sucesso.",
                    candidatura
            )
    );
}

@PatchMapping("/{candidaturaId}/reject")
public ResponseEntity<ApiResponse<CandidaturaResponse>> recusar(
        @PathVariable Long candidaturaId
) {

    CandidaturaResponse candidatura =
            candidaturaService.recusar(candidaturaId);

    return ResponseEntity.ok(
            ApiResponse.success(
                    "Candidatura recusada com sucesso.",
                    candidatura
            )
    );
}
}