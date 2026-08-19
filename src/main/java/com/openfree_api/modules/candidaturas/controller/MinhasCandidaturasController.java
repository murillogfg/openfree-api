package com.openfree_api.modules.candidaturas.controller;

import com.openfree_api.common.response.ApiResponse;
import com.openfree_api.modules.candidaturas.dto.MyApplicationResponse;
import com.openfree_api.modules.candidaturas.service.CandidaturaService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/applications")
public class MinhasCandidaturasController {

    private final CandidaturaService candidaturaService;

    public MinhasCandidaturasController(
            CandidaturaService candidaturaService
    ) {
        this.candidaturaService = candidaturaService;
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