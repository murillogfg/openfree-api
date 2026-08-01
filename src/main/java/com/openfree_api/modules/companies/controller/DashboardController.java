package com.openfree_api.modules.companies.controller;

import com.openfree_api.common.response.ApiResponse;
import com.openfree_api.modules.companies.dto.DashboardEmpresaResponse;
import com.openfree_api.modules.companies.service.DashboardService;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/dashboard/company")
public class DashboardController {

    public DashboardController(
            DashboardService dashboardService
    ) {
    }

    @GetMapping
   public ResponseEntity<ApiResponse<DashboardEmpresaResponse>> dashboard(
        Authentication authentication
){

       DashboardEmpresaResponse response =
        new DashboardEmpresaResponse();

        return ResponseEntity.ok(
                ApiResponse.success(
                        "Dashboard carregado com sucesso.",
                        response
                )
        );
    }
}
