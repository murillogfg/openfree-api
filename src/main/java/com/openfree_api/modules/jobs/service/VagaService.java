package com.openfree_api.modules.jobs.service;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.companies.repository.EmpresaRepository;
import com.openfree_api.modules.jobs.dto.CreateVagaRequest;
import com.openfree_api.modules.jobs.dto.VagaResponse;
import com.openfree_api.modules.jobs.entity.Vaga;
import com.openfree_api.modules.jobs.mapper.VagaMapper;
import com.openfree_api.modules.jobs.repository.VagaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VagaService {

    private final VagaRepository vagaRepository;
    private final EmpresaRepository empresaRepository;
    private final VagaMapper vagaMapper;

    public VagaService(
            VagaRepository vagaRepository,
            EmpresaRepository empresaRepository,
            VagaMapper vagaMapper
    ) {
        this.vagaRepository = vagaRepository;
        this.empresaRepository = empresaRepository;
        this.vagaMapper = vagaMapper;
    }

    public List<VagaResponse> listarTodas() {
        return vagaRepository.findAll()
                .stream()
                .map(vagaMapper::toResponse)
                .toList();
    }

    public Optional<VagaResponse> buscarPorId(Long id) {
        return vagaRepository.findById(id)
                .map(vagaMapper::toResponse);
    }

    public List<VagaResponse> listarPorEmpresa(Long empresaId) {

        if (!empresaRepository.existsById(empresaId)) {
            throw new BusinessException(
                    "Empresa não encontrada."
            );
        }

        return vagaRepository.findByEmpresaId(empresaId)
                .stream()
                .map(vagaMapper::toResponse)
                .toList();
    }

    public VagaResponse criar(CreateVagaRequest request) {

        Empresa empresa = empresaRepository
                .findById(request.getEmpresaId())
                .orElseThrow(() ->
                        new BusinessException(
                                "Empresa não encontrada."
                        )
                );

        if (!request.getHorarioFim()
                .isAfter(request.getHorarioInicio())) {

            throw new BusinessException(
                    "O horário de término deve ser posterior ao horário de início."
            );
        }

        Vaga vaga = vagaMapper.toEntity(request);

        vaga.setEmpresa(empresa);

        Vaga vagaSalva = vagaRepository.save(vaga);

        return vagaMapper.toResponse(vagaSalva);
    }

    public boolean excluir(Long id) {

        if (!vagaRepository.existsById(id)) {
            return false;
        }

        vagaRepository.deleteById(id);

        return true;
    }
}