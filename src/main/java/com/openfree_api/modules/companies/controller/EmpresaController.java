package com.openfree_api.modules.companies.controller;

import com.openfree_api.common.response.ApiResponse;
import com.openfree_api.modules.companies.dto.CreateEmpresaRequest;
import com.openfree_api.modules.companies.dto.EmpresaResponse;
import com.openfree_api.modules.companies.dto.EmpresaUsuarioResponse;
import com.openfree_api.modules.companies.service.EmpresaService;
import jakarta.validation.Valid;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.openfree_api.modules.companies.dto.EmpresaUsuarioResponse;

@RestController
@RequestMapping("/companies")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmpresaResponse>>> listarTodas() {

        List<EmpresaResponse> empresas = empresaService.listarTodas();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Empresas listadas com sucesso.",
                        empresas
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmpresaResponse>> buscarPorId(
            @PathVariable Long id
    ) {
        return empresaService.buscarPorId(id)
                .map(empresa ->
                        ResponseEntity.ok(
                                ApiResponse.success(
                                        "Empresa encontrada com sucesso.",
                                        empresa
                                )
                        )
                )
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<EmpresaResponse>> criar(
            @Valid @RequestBody CreateEmpresaRequest request
    ) {
        EmpresaResponse empresa = empresaService.criar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Empresa criada com sucesso.",
                                empresa
                        )
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {

        boolean excluida = empresaService.excluir(id);

        if (!excluida) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{empresaId}/members")
public ResponseEntity<ApiResponse<List<EmpresaUsuarioResponse>>> listarMembros(
        @PathVariable Long empresaId
) {

    List<EmpresaUsuarioResponse> membros =
            empresaService.listarMembros(empresaId);

    return ResponseEntity.ok(
            ApiResponse.success(
                    "Membros da empresa listados com sucesso.",
                    membros
            )
    );
}
}