package com.openfree_api.modules.jobs.controller;

import com.openfree_api.common.response.ApiResponse;
import com.openfree_api.common.response.PageResponse;

import com.openfree_api.modules.jobs.dto.CreateVagaRequest;
import com.openfree_api.modules.jobs.dto.JobFilterRequest;
import com.openfree_api.modules.jobs.dto.VagaResponse;

import com.openfree_api.modules.jobs.service.VagaService;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/jobs")
public class VagaController {

    private final VagaService vagaService;


    public VagaController(
            VagaService vagaService
    ) {
        this.vagaService =
                vagaService;
    }


    /*
     * =====================================================
     * LISTAGEM PÚBLICA
     * =====================================================
     *
     * Mesmo que alguém envie:
     *
     * ?status=RASCUNHO
     *
     * o service força PUBLICADA.
     *
     * Isso é importante porque este endpoint
     * foi aberto para visitantes e mecanismos
     * de busca.
     */
    @GetMapping
    public ResponseEntity<
            ApiResponse<
                    PageResponse<VagaResponse>
            >
    > buscar(
            JobFilterRequest filtro,

            @PageableDefault(
                    page = 0,
                    size = 10,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            )
            Pageable pageable
    ) {

        PageResponse<VagaResponse> vagas =
                vagaService.buscarPublicadas(
                        filtro,
                        pageable
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Vagas listadas com sucesso.",
                        vagas
                )
        );
    }


    /*
     * =====================================================
     * DETALHE PÚBLICO
     * =====================================================
     *
     * Retorna somente uma vaga PUBLICADA.
     *
     * Rascunhos e vagas com outros status
     * não ficam disponíveis publicamente
     * apenas pelo ID.
     */
    @GetMapping("/{id}")
    public ResponseEntity<
            ApiResponse<VagaResponse>
    > buscarPorId(
            @PathVariable Long id
    ) {

        return vagaService
                .buscarPublicadaPorId(id)
                .map(vaga ->
                        ResponseEntity.ok(
                                ApiResponse.success(
                                        "Vaga encontrada com sucesso.",
                                        vaga
                                )
                        )
                )
                .orElse(
                        ResponseEntity
                                .notFound()
                                .build()
                );
    }


    @PostMapping
    public ResponseEntity<
            ApiResponse<VagaResponse>
    > criar(
            @Valid
            @RequestBody
            CreateVagaRequest request,

            Authentication authentication
    ) {

        VagaResponse vaga =
                vagaService.criar(
                        request,
                        authentication
                );

        return ResponseEntity
                .status(
                        HttpStatus.CREATED
                )
                .body(
                        ApiResponse.success(
                                "Vaga criada com sucesso.",
                                vaga
                        )
                );
    }


    @PatchMapping("/{id}/publicar")
    public ResponseEntity<
            ApiResponse<VagaResponse>
    > publicar(
            @PathVariable Long id,
            Authentication authentication
    ) {

        VagaResponse vaga =
                vagaService.publicar(
                        id,
                        authentication
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Vaga publicada com sucesso.",
                        vaga
                )
        );
    }
}