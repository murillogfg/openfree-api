package com.openfree_api.modules.profile.controller;

import com.openfree_api.common.response.ApiResponse;
import com.openfree_api.modules.profile.dto.CompanyProfileResponse;
import com.openfree_api.modules.profile.dto.FreelancerProfileResponse;
import com.openfree_api.modules.profile.dto.UpdateCompanyProfileRequest;
import com.openfree_api.modules.profile.dto.UpdateFreelancerProfileRequest;
import com.openfree_api.modules.profile.service.ProfileService;

import jakarta.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/profile")
public class ProfileController {

    private final ProfileService profileService;

    public ProfileController(
            ProfileService profileService
    ) {
        this.profileService = profileService;
    }

    @GetMapping("/freelancer/me")
    public ResponseEntity<ApiResponse<FreelancerProfileResponse>>
    buscarPerfilFreelancer(
            Authentication authentication
    ) {

        FreelancerProfileResponse response =
                profileService.buscarPerfilFreelancer(
                        authentication
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Perfil do freelancer carregado com sucesso.",
                        response
                )
        );
    }

    @PatchMapping("/freelancer")
    public ResponseEntity<ApiResponse<FreelancerProfileResponse>>
    atualizarPerfilFreelancer(
            @Valid @RequestBody
            UpdateFreelancerProfileRequest request,
            Authentication authentication
    ) {

        FreelancerProfileResponse response =
                profileService.atualizarPerfilFreelancer(
                        request,
                        authentication
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Perfil do freelancer atualizado com sucesso.",
                        response
                )
        );
    }

    @GetMapping("/company/me")
    public ResponseEntity<ApiResponse<CompanyProfileResponse>>
    buscarPerfilEmpresa(
            Authentication authentication
    ) {

        CompanyProfileResponse response =
                profileService.buscarPerfilEmpresa(
                        authentication
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Perfil da empresa carregado com sucesso.",
                        response
                )
        );
    }

    @PatchMapping("/company")
    public ResponseEntity<ApiResponse<CompanyProfileResponse>>
    atualizarPerfilEmpresa(
            @Valid @RequestBody
            UpdateCompanyProfileRequest request,
            Authentication authentication
    ) {

        CompanyProfileResponse response =
                profileService.atualizarPerfilEmpresa(
                        request,
                        authentication
                );

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Perfil da empresa atualizado com sucesso.",
                        response
                )
        );
    }

    @PostMapping(
        value = "/freelancer/avatar",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
)
public ResponseEntity<ApiResponse<FreelancerProfileResponse>>
atualizarAvatar(
        @RequestPart("file") MultipartFile file,
        Authentication authentication
) {

    FreelancerProfileResponse response =
            profileService.atualizarAvatar(
                    file,
                    authentication
            );

    return ResponseEntity.ok(
            ApiResponse.success(
                    "Foto de perfil atualizada com sucesso.",
                    response
            )
    );
}

@PostMapping(
        value = "/freelancer/resume",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
)
public ResponseEntity<ApiResponse<FreelancerProfileResponse>>
atualizarCurriculo(
        @RequestPart("file") MultipartFile file,
        Authentication authentication
) {

    FreelancerProfileResponse response =
            profileService.atualizarCurriculo(
                    file,
                    authentication
            );

    return ResponseEntity.ok(
            ApiResponse.success(
                    "Currículo atualizado com sucesso.",
                    response
            )
    );
}

@PostMapping(
        value = "/company/logo",
        consumes = MediaType.MULTIPART_FORM_DATA_VALUE
)
public ResponseEntity<ApiResponse<CompanyProfileResponse>>
atualizarLogo(
        @RequestPart("file") MultipartFile file,
        Authentication authentication
) {

    CompanyProfileResponse response =
            profileService.atualizarLogo(
                    file,
                    authentication
            );

    return ResponseEntity.ok(
            ApiResponse.success(
                    "Logo da empresa atualizada com sucesso.",
                    response
            )
    );
}

}