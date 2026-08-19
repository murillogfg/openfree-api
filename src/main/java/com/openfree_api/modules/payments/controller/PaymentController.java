package com.openfree_api.modules.payments.controller;

import com.openfree_api.common.response.ApiResponse;

import com.openfree_api.modules.payments.dto.PaymentResponse;
import com.openfree_api.modules.payments.dto.SimulatePaymentRequest;
import com.openfree_api.modules.payments.service.PaymentService;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;

    public PaymentController(
            PaymentService paymentService
    ) {
        this.paymentService =
                paymentService;
    }

    @GetMapping("/me")
    public ResponseEntity<
            ApiResponse<List<PaymentResponse>>
            > listarMeusPagamentos(
            Authentication authentication
    ) {

        List<PaymentResponse> pagamentos =
                paymentService
                        .listarMeusPagamentos(
                                authentication
                        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Pagamentos listados com sucesso.",
                        pagamentos
                )
        );
    }

    @GetMapping("/company")
    public ResponseEntity<
            ApiResponse<List<PaymentResponse>>
            > listarPagamentosEmpresa(
            Authentication authentication
    ) {

        List<PaymentResponse> pagamentos =
                paymentService
                        .listarPagamentosDaEmpresa(
                                authentication
                        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Pagamentos da empresa listados com sucesso.",
                        pagamentos
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<PaymentResponse>>
    buscarPorId(
            @PathVariable Long id,
            Authentication authentication
    ) {

        PaymentResponse pagamento =
                paymentService.buscarPorId(
                        id,
                        authentication
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Pagamento encontrado com sucesso.",
                        pagamento
                )
        );
    }

    /*
     * Endpoint temporário do MVP.
     *
     * Depois será substituído pelo fluxo
     * do gateway + webhook.
     */
    @PostMapping("/{id}/simulate-payment")
    public ResponseEntity<ApiResponse<PaymentResponse>>
    simularPagamento(
            @PathVariable Long id,
            @Valid
            @RequestBody
            SimulatePaymentRequest request,
            Authentication authentication
    ) {

        PaymentResponse pagamento =
                paymentService.simularPagamento(
                        id,
                        request.getMetodo(),
                        authentication
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Pagamento simulado com sucesso.",
                        pagamento
                )
        );
    }

    @PatchMapping("/{id}/release")
    public ResponseEntity<ApiResponse<PaymentResponse>>
    liberarPagamento(
            @PathVariable Long id,
            Authentication authentication
    ) {

        PaymentResponse pagamento =
                paymentService.liberarPagamento(
                        id,
                        authentication
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Pagamento liberado com sucesso.",
                        pagamento
                )
        );
    }
}