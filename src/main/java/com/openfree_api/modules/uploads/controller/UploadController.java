package com.openfree_api.modules.uploads.controller;

import com.openfree_api.common.response.ApiResponse;
import com.openfree_api.modules.auth.service.EmpresaAuthService;
import com.openfree_api.modules.auth.service.UsuarioAuthService;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.companies.repository.EmpresaRepository;
import com.openfree_api.modules.uploads.dto.UploadResponse;
import com.openfree_api.modules.uploads.service.FileStorageService;
import com.openfree_api.modules.users.entity.Usuario;
import com.openfree_api.modules.users.repository.UsuarioRepository;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/uploads")
public class UploadController {

    private final FileStorageService fileStorageService;
    private final UsuarioAuthService usuarioAuthService;
    private final EmpresaAuthService empresaAuthService;
    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;

    public UploadController(
            FileStorageService fileStorageService,
            UsuarioAuthService usuarioAuthService,
            EmpresaAuthService empresaAuthService,
            UsuarioRepository usuarioRepository,
            EmpresaRepository empresaRepository
    ) {
        this.fileStorageService = fileStorageService;
        this.usuarioAuthService = usuarioAuthService;
        this.empresaAuthService = empresaAuthService;
        this.usuarioRepository = usuarioRepository;
        this.empresaRepository = empresaRepository;
    }

    @PostMapping("/avatar")
    public ResponseEntity<ApiResponse<UploadResponse>> uploadAvatar(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService.getUsuarioLogado(
                        authentication
                );

        UploadResponse upload =
                fileStorageService.salvarImagem(
                        file,
                        "avatars"
                );

        usuario.setAvatarUrl(
                upload.getUrl()
        );

        usuarioRepository.save(
                usuario
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Avatar atualizado com sucesso.",
                        upload
                )
        );
    }

    @PostMapping("/curriculum")
    public ResponseEntity<ApiResponse<UploadResponse>> uploadCurriculo(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService.getUsuarioLogado(
                        authentication
                );

        UploadResponse upload =
                fileStorageService.salvarPdf(
                        file,
                        "curriculos"
                );

        usuario.setCurriculoUrl(
                upload.getUrl()
        );

        usuarioRepository.save(
                usuario
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Currículo atualizado com sucesso.",
                        upload
                )
        );
    }

    @PostMapping("/company-logo")
    public ResponseEntity<ApiResponse<UploadResponse>> uploadLogoEmpresa(
            @RequestParam("file") MultipartFile file,
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService.getEmpresaLogada(
                        authentication
                );

        UploadResponse upload =
                fileStorageService.salvarImagem(
                        file,
                        "logos"
                );

        empresa.setLogo(
                upload.getUrl()
        );

        empresaRepository.save(
                empresa
        );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Logo da empresa atualizado com sucesso.",
                        upload
                )
        );
    }
}