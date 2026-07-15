package com.openfree_api.modules.users.controller;

import com.openfree_api.common.response.ApiResponse;
import com.openfree_api.modules.users.dto.CreateUsuarioRequest;
import com.openfree_api.modules.users.dto.UsuarioResponse;
import com.openfree_api.modules.users.service.UsuarioService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
}