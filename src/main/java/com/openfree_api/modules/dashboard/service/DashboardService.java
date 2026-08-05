package com.openfree_api.modules.dashboard.service;

import com.openfree_api.modules.auth.service.EmpresaAuthService;
import com.openfree_api.modules.companies.dto.CompanyDashboardResponse;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.candidaturas.entity.StatusCandidatura;
import com.openfree_api.modules.candidaturas.repository.CandidaturaRepository;
import com.openfree_api.modules.jobs.entity.StatusVaga;
import com.openfree_api.modules.jobs.repository.VagaRepository;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class DashboardService {

    private final EmpresaAuthService empresaAuthService;
    private final VagaRepository vagaRepository;
    private final CandidaturaRepository candidaturaRepository;

    public DashboardService(
            EmpresaAuthService empresaAuthService,
            VagaRepository vagaRepository,
            CandidaturaRepository candidaturaRepository
    ) {
        this.empresaAuthService = empresaAuthService;
        this.vagaRepository = vagaRepository;
        this.candidaturaRepository = candidaturaRepository;
    }

    public CompanyDashboardResponse dashboard(
            Authentication authentication
    ) {

        Empresa empresa =
                empresaAuthService.getEmpresaLogada(authentication);

        CompanyDashboardResponse response =
                new CompanyDashboardResponse();

        response.setVagasPublicadas(
                vagaRepository.countByEmpresaId(
                        empresa.getId()
                )
        );

        response.setVagasAbertas(
                vagaRepository.countByEmpresaIdAndStatus(
                        empresa.getId(),
                        StatusVaga.PUBLICADA
                )
        );

        response.setVagasFinalizadas(
                vagaRepository.countByEmpresaIdAndStatus(
                        empresa.getId(),
                        StatusVaga.FINALIZADA
                )
        );

        response.setCandidaturasRecebidas(
                candidaturaRepository.countByVagaEmpresaId(
                        empresa.getId()
                )
        );

        response.setCandidaturasPendentes(
                candidaturaRepository.countByVagaEmpresaIdAndStatus(
                        empresa.getId(),
                        StatusCandidatura.PENDENTE
                )
        );

        response.setCandidaturasAceitas(
                candidaturaRepository.countByVagaEmpresaIdAndStatus(
                        empresa.getId(),
                        StatusCandidatura.ACEITA
                )
        );

        response.setCandidaturasRecusadas(
                candidaturaRepository.countByVagaEmpresaIdAndStatus(
                        empresa.getId(),
                        StatusCandidatura.RECUSADA
                )
        );

        return response;
    }
}