package com.openfree_api.modules.jobs.controller;

import com.openfree_api.common.response.ApiResponse;
import com.openfree_api.modules.jobs.dto.CreateVagaRequest;
import com.openfree_api.modules.jobs.dto.VagaResponse;
import com.openfree_api.modules.jobs.service.VagaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;



import java.util.List;

@RestController
@RequestMapping("/jobs")
public class VagaController {

    private final VagaService vagaService;

    public VagaController(VagaService vagaService) {
        this.vagaService = vagaService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<VagaResponse>>> listarTodas() {

        List<VagaResponse> vagas = vagaService.listarTodas();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Vagas listadas com sucesso.",
                        vagas
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<VagaResponse>> buscarPorId(
            @PathVariable Long id
    ) {

        return vagaService.buscarPorId(id)
                .map(vaga ->
                        ResponseEntity.ok(
                                ApiResponse.success(
                                        "Vaga encontrada com sucesso.",
                                        vaga
                                )
                        )
                )
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<VagaResponse>> criar(
            @Valid @RequestBody CreateVagaRequest request
    ) {

        VagaResponse vaga = vagaService.criar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Vaga criada com sucesso.",
                                vaga
                        )
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {

        boolean excluida = vagaService.excluir(id);

        if (!excluida) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}