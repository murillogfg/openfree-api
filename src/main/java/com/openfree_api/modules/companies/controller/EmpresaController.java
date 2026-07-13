package com.openfree_api.modules.companies.controller;

import com.openfree_api.modules.companies.dto.CreateEmpresaRequest;
import com.openfree_api.modules.companies.dto.EmpresaResponse;
import com.openfree_api.modules.companies.service.EmpresaService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/companies")
public class EmpresaController {

    private final EmpresaService empresaService;

    public EmpresaController(EmpresaService empresaService) {
        this.empresaService = empresaService;
    }

    @GetMapping
    public ResponseEntity<List<EmpresaResponse>> listarTodas() {
        return ResponseEntity.ok(empresaService.listarTodas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<EmpresaResponse> buscarPorId(@PathVariable Long id) {
        return empresaService.buscarPorId(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<EmpresaResponse> criar(
            @Valid @RequestBody CreateEmpresaRequest request
    ) {
        EmpresaResponse response = empresaService.criar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Long id) {
        boolean excluida = empresaService.excluir(id);

        if (!excluida) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }
}