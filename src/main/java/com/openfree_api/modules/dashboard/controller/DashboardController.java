package com.openfree_api.modules.dashboard.controller;

import com.openfree_api.common.response.ApiResponse;
import com.openfree_api.modules.dashboard.dto.DashboardEmpresaResponse;
import com.openfree_api.modules.dashboard.dto.DashboardFreelancerResponse;
import com.openfree_api.modules.dashboard.service.DashboardEmpresaService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import com.openfree_api.modules.dashboard.service.DashboardFreelancerService;



@RestController
@RequestMapping("/dashboard")
public class DashboardController {

    private final DashboardFreelancerService dashboardFreelancerService;
    private final DashboardEmpresaService dashboardEmpresaService;

    public DashboardController(
            DashboardFreelancerService dashboardFreelancerService,
            DashboardEmpresaService dashboardEmpresaService
    ) {
        this.dashboardFreelancerService = dashboardFreelancerService;
        this.dashboardEmpresaService = dashboardEmpresaService;
    }

    @GetMapping("/freelancer")
    public ResponseEntity<ApiResponse<DashboardFreelancerResponse>>
    dashboardFreelancer(Authentication authentication) {

        DashboardFreelancerResponse response =
                dashboardFreelancerService.dashboard(authentication);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Dashboard do freelancer carregado com sucesso.",
                        response
                )
        );
    }

    @GetMapping("/company")
    public ResponseEntity<ApiResponse<DashboardEmpresaResponse>>
    dashboardEmpresa(Authentication authentication) {

        DashboardEmpresaResponse response =
                dashboardEmpresaService.dashboard(authentication);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Dashboard da empresa carregado com sucesso.",
                        response
                )
        );
    }
}