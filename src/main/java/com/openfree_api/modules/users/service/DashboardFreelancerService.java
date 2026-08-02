package com.openfree_api.modules.users.service;

import com.openfree_api.modules.auth.service.UsuarioAuthService;
import com.openfree_api.modules.candidaturas.entity.StatusCandidatura;
import com.openfree_api.modules.candidaturas.repository.CandidaturaRepository;
import com.openfree_api.modules.users.dto.DashboardFreelancerResponse;
import com.openfree_api.modules.users.entity.Usuario;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

@Service
public class DashboardFreelancerService {

    private final UsuarioAuthService usuarioAuthService;
    private final CandidaturaRepository candidaturaRepository;

    public DashboardFreelancerService(
            UsuarioAuthService usuarioAuthService,
            CandidaturaRepository candidaturaRepository
    ) {
        this.usuarioAuthService = usuarioAuthService;
        this.candidaturaRepository = candidaturaRepository;
    }

    public DashboardFreelancerResponse dashboard(
            Authentication authentication
    ) {

        Usuario usuario =
                usuarioAuthService.getUsuarioLogado(authentication);

        DashboardFreelancerResponse response =
                new DashboardFreelancerResponse();

        response.setCandidaturasEnviadas(
                candidaturaRepository.countByUsuarioId(
                        usuario.getId()
                )
        );

        response.setPendentes(
                candidaturaRepository.countByUsuarioIdAndStatus(
                        usuario.getId(),
                        StatusCandidatura.PENDENTE
                )
        );

        response.setAceitas(
                candidaturaRepository.countByUsuarioIdAndStatus(
                        usuario.getId(),
                        StatusCandidatura.ACEITA
                )
        );

        response.setRecusadas(
                candidaturaRepository.countByUsuarioIdAndStatus(
                        usuario.getId(),
                        StatusCandidatura.RECUSADA
                )
        );

        response.setTrabalhosConcluidos(
                candidaturaRepository.countByUsuarioIdAndStatus(
                        usuario.getId(),
                        StatusCandidatura.ACEITA
                )
        );

        return response;
    }
}