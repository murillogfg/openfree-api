package com.openfree_api.modules.candidaturas.service;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.candidaturas.dto.CandidaturaResponse;
import com.openfree_api.modules.candidaturas.dto.CreateCandidaturaRequest;
import com.openfree_api.modules.candidaturas.entity.Candidatura;
import com.openfree_api.modules.candidaturas.entity.StatusCandidatura;
import com.openfree_api.modules.candidaturas.mapper.CandidaturaMapper;
import com.openfree_api.modules.candidaturas.repository.CandidaturaRepository;
import com.openfree_api.modules.jobs.entity.StatusVaga;
import com.openfree_api.modules.jobs.entity.Vaga;
import com.openfree_api.modules.jobs.repository.VagaRepository;
import com.openfree_api.modules.users.entity.Usuario;
import com.openfree_api.modules.users.repository.UsuarioRepository;
import java.util.List;
import org.springframework.stereotype.Service;





@Service
public class CandidaturaService {

    private final CandidaturaRepository candidaturaRepository;
    private final UsuarioRepository usuarioRepository;
    private final VagaRepository vagaRepository;
    private final CandidaturaMapper candidaturaMapper;

    public CandidaturaService(
            CandidaturaRepository candidaturaRepository,
            UsuarioRepository usuarioRepository,
            VagaRepository vagaRepository,
            CandidaturaMapper candidaturaMapper
    ) {
        this.candidaturaRepository = candidaturaRepository;
        this.usuarioRepository = usuarioRepository;
        this.vagaRepository = vagaRepository;
        this.candidaturaMapper = candidaturaMapper;
    }
  public CandidaturaResponse criar(
        Long vagaId,
        CreateCandidaturaRequest request
) {

    Usuario usuario = usuarioRepository
            .findById(request.getUsuarioId())
            .orElseThrow(() ->
                    new BusinessException("Usuário não encontrado.")
            );

    Vaga vaga = vagaRepository
            .findById(vagaId)
            .orElseThrow(() ->
                    new BusinessException("Vaga não encontrada.")
            );

    if (vaga.getStatus() != StatusVaga.PUBLICADA) {
        throw new BusinessException(
                "A vaga não está disponível para candidatura."
        );
    }

    if (candidaturaRepository.existsByVagaIdAndUsuarioId(
            vagaId,
            usuario.getId()
    )) {
        throw new BusinessException(
                "Você já se candidatou para esta vaga."
        );
    }

    Candidatura candidatura =
            candidaturaMapper.toEntity(request);

    candidatura.setUsuario(usuario);
    candidatura.setVaga(vaga);

    Candidatura candidaturaSalva =
            candidaturaRepository.save(candidatura);

    return candidaturaMapper.toResponse(candidaturaSalva);
}
public List<CandidaturaResponse> listarPorVaga(Long vagaId) {

    if (!vagaRepository.existsById(vagaId)) {
        throw new BusinessException("Vaga não encontrada.");
    }

    return candidaturaRepository
            .findByVagaId(vagaId)
            .stream()
            .map(candidaturaMapper::toResponse)
            .toList();
}

    public CandidaturaResponse aceitar(Long candidaturaId) {

    Candidatura candidatura = candidaturaRepository
            .findById(candidaturaId)
            .orElseThrow(() ->
                    new BusinessException("Candidatura não encontrada.")
            );

    if (
            candidatura.getStatus() != StatusCandidatura.PENDENTE
            && candidatura.getStatus() != StatusCandidatura.VISUALIZADA
    ) {
        throw new BusinessException(
                "Somente candidaturas pendentes ou visualizadas podem ser aceitas."
        );
    }

    Vaga vaga = candidatura.getVaga();

    if (vaga.getStatus() != StatusVaga.PUBLICADA) {
        throw new BusinessException(
                "Esta vaga não está mais disponível para aceitar candidatos."
        );
    }

    long quantidadeAceitos = candidaturaRepository
            .countByVagaIdAndStatus(
                    vaga.getId(),
                    StatusCandidatura.ACEITA
            );

    if (quantidadeAceitos >= vaga.getQuantidadePessoas()) {
        throw new BusinessException(
                "A quantidade necessária de profissionais já foi preenchida."
        );
    }

    candidatura.setStatus(StatusCandidatura.ACEITA);
    candidatura.setEmpresaVisualizou(true);

    Candidatura candidaturaSalva =
            candidaturaRepository.save(candidatura);

    quantidadeAceitos++;

    if (quantidadeAceitos >= vaga.getQuantidadePessoas()) {

        vaga.setStatus(StatusVaga.FINALIZADA);
        vagaRepository.save(vaga);

        List<Candidatura> restantes =
                candidaturaRepository.findByVagaIdAndStatusIn(
                        vaga.getId(),
                        List.of(
                                StatusCandidatura.PENDENTE,
                                StatusCandidatura.VISUALIZADA
                        )
                );

        restantes.forEach(restante -> {
            restante.setStatus(StatusCandidatura.RECUSADA);
            restante.setEmpresaVisualizou(true);
        });

        candidaturaRepository.saveAll(restantes);
    }

    return candidaturaMapper.toResponse(candidaturaSalva);
}
    public CandidaturaResponse recusar(Long candidaturaId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'recusar'");
    }
}