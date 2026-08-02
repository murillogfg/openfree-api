package com.openfree_api.modules.users.controller;

import com.openfree_api.common.response.ApiResponse;
import com.openfree_api.modules.users.dto.DashboardFreelancerResponse;
import com.openfree_api.modules.users.service.DashboardFreelancerService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dashboard/freelancer")
public class DashboardFreelancerController {

    private final DashboardFreelancerService dashboardFreelancerService;

    public DashboardFreelancerController(
            DashboardFreelancerService dashboardFreelancerService
    ) {
        this.dashboardFreelancerService = dashboardFreelancerService;
    }

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardFreelancerResponse>> dashboard(
            Authentication authentication
    ) {

        DashboardFreelancerResponse response =
                dashboardFreelancerService.dashboard(authentication);

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Dashboard carregado com sucesso.",
                        response
                )
        );
    }
}