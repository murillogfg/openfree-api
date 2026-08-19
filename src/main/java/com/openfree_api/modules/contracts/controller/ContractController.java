package com.openfree_api.modules.contracts.controller;

import com.openfree_api.common.response.ApiResponse;
import com.openfree_api.modules.contracts.dto.ContractResponse;

import com.openfree_api.modules.contracts.service.ContractService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/contracts")
public class ContractController {

    private final ContractService contractService;

    public ContractController(
            ContractService contractService
    ) {
        this.contractService =
                contractService;
    }

    @GetMapping("/me")
    public ResponseEntity<
            ApiResponse<List<ContractResponse>>
            > listarMeusContratos(
            Authentication authentication
    ) {

        List<ContractResponse> contratos =
                contractService
                        .listarMeusContratos(
                                authentication
                        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Contratos listados com sucesso.",
                        contratos
                )
        );
    }

    @GetMapping("/company")
    public ResponseEntity<
            ApiResponse<List<ContractResponse>>
            > listarContratosEmpresa(
            Authentication authentication
    ) {

        List<ContractResponse> contratos =
                contractService
                        .listarContratosDaEmpresa(
                                authentication
                        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Contratos da empresa listados com sucesso.",
                        contratos
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<
            ApiResponse<ContractResponse>
            > buscarPorId(
            @PathVariable Long id,
            Authentication authentication
    ) {

        ContractResponse contrato =
                contractService.buscarPorId(
                        id,
                        authentication
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Contrato encontrado com sucesso.",
                        contrato
                )
        );
    }

    @PatchMapping("/{id}/start")
    public ResponseEntity<
            ApiResponse<ContractResponse>
            > iniciar(
            @PathVariable Long id,
            Authentication authentication
    ) {

        ContractResponse contrato =
                contractService.iniciarContrato(
                        id,
                        authentication
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Contrato iniciado com sucesso.",
                        contrato
                )
        );
    }

   @PatchMapping("/{id}/complete/company")
public ResponseEntity<ApiResponse<ContractResponse>>
confirmarConclusaoEmpresa(
        @PathVariable Long id,
        Authentication authentication
) {

    ContractResponse contrato =
            contractService
                    .confirmarConclusaoEmpresa(
                            id,
                            authentication
                    );

    return ResponseEntity.ok(
            ApiResponse.success(
                    "Conclusão confirmada pela empresa.",
                    contrato
            )
    );
}

    @PatchMapping("/{id}/complete/freelancer")
public ResponseEntity<ApiResponse<ContractResponse>>
confirmarConclusaoFreelancer(
        @PathVariable Long id,
        Authentication authentication
) {

    ContractResponse contrato =
            contractService
                    .confirmarConclusaoFreelancer(
                            id,
                            authentication
                    );

    return ResponseEntity.ok(
            ApiResponse.success(
                    "Conclusão confirmada pelo profissional.",
                    contrato
            )
    );
}
}
