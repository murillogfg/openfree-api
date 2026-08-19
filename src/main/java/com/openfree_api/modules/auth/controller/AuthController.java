package com.openfree_api.modules.auth.controller;

import com.openfree_api.modules.auth.dto.LoginRequest;
import com.openfree_api.modules.auth.dto.LoginResponse;
import com.openfree_api.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.openfree_api.common.response.ApiResponse;
import com.openfree_api.modules.auth.dto.ForgotPasswordRequest;
import com.openfree_api.modules.auth.dto.ResetPasswordRequest;
import com.openfree_api.modules.auth.service.PasswordResetService;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;
    private final PasswordResetService passwordResetService;


   public AuthController(
        AuthService authService,
        PasswordResetService passwordResetService
) {
    this.authService =
            authService;

    this.passwordResetService =
            passwordResetService;
}

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ResponseEntity.ok(
                authService.login(request)
        );
    }

    @PostMapping("/forgot-password")
public ResponseEntity<ApiResponse<Void>>
solicitarRecuperacao(
        @Valid
        @RequestBody
        ForgotPasswordRequest request
) {

    passwordResetService
            .solicitarRecuperacao(
                    request.getEmail()
            );

    return ResponseEntity.ok(
            ApiResponse.success(
                    "Se o e-mail estiver cadastrado, enviaremos as instruções para recuperação da senha.",
                    null
            )
    );
}

@PostMapping("/reset-password")
public ResponseEntity<ApiResponse<Void>>
redefinirSenha(
        @Valid
        @RequestBody
        ResetPasswordRequest request
) {

    passwordResetService
            .redefinirSenha(
                    request.getToken(),
                    request.getNovaSenha()
            );

    return ResponseEntity.ok(
            ApiResponse.success(
                    "Senha redefinida com sucesso.",
                    null
            )
    );
}
}