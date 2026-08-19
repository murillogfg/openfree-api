package com.openfree_api.modules.users.controller;

import com.openfree_api.common.response.ApiResponse;
import com.openfree_api.modules.users.dto.CreateUsuarioRequest;
import com.openfree_api.modules.users.dto.UsuarioResponse;
import com.openfree_api.modules.users.service.UsuarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;

import com.openfree_api.modules.users.dto.UpdateUsuarioRequest;
import com.openfree_api.modules.users.dto.UpdateUsuarioRequest;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;


@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<UsuarioResponse>>> listarTodos() {

        List<UsuarioResponse> usuarios = usuarioService.listarTodos();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Usuários listados com sucesso.",
                        usuarios
                )
        );
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UsuarioResponse>> buscarPorId(
            @PathVariable Long id
    ) {
        return usuarioService.buscarPorId(id)
                .map(usuario ->
                        ResponseEntity.ok(
                                ApiResponse.success(
                                        "Usuário encontrado com sucesso.",
                                        usuario
                                )
                        )
                )
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UsuarioResponse>> criar(
            @RequestBody CreateUsuarioRequest request
    ) {

        UsuarioResponse usuario = usuarioService.criar(request);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        ApiResponse.success(
                                "Usuário criado com sucesso.",
                                usuario
                        )
                );
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(
            @PathVariable Long id
    ) {

        boolean excluido = usuarioService.excluir(id);

        if (!excluido) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{id}/profile")
public ResponseEntity<ApiResponse<UsuarioResponse>>
buscarPerfilPublico(
        @PathVariable Long id
) {

    UsuarioResponse usuario =
            usuarioService.buscarPerfilPublico(
                    id
            );

    return ResponseEntity.ok(
            ApiResponse.success(
                    "Perfil público carregado com sucesso.",
                    usuario
            )
    );
}




 @GetMapping("/me")
public ResponseEntity<ApiResponse<UsuarioResponse>>
buscarMeuPerfil(
        Authentication authentication
) {

    UsuarioResponse usuario =
            usuarioService.buscarMeuPerfil(
                    authentication
            );

    return ResponseEntity.ok(
            ApiResponse.success(
                    "Perfil carregado com sucesso.",
                    usuario
            )
    );
}

@PatchMapping("/me")
public ResponseEntity<ApiResponse<UsuarioResponse>>
atualizarMeuPerfil(
        @Valid
        @RequestBody
        UpdateUsuarioRequest request,
        Authentication authentication
) {

    UsuarioResponse usuario =
            usuarioService.atualizarMeuPerfil(
                    request,
                    authentication
            );

    return ResponseEntity.ok(
            ApiResponse.success(
                    "Perfil atualizado com sucesso.",
                    usuario
            )
    );
}
}