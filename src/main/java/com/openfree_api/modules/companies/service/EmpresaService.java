package com.openfree_api.modules.companies.service;

import com.openfree_api.modules.companies.dto.CreateEmpresaRequest;
import com.openfree_api.modules.companies.dto.EmpresaResponse;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.companies.mapper.EmpresaMapper;
import com.openfree_api.modules.companies.repository.EmpresaRepository;
import org.springframework.stereotype.Service;
import com.openfree_api.common.exception.BusinessException;




import java.util.List;
import java.util.Optional;

@Service
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper empresaMapper;

    public EmpresaService(
            EmpresaRepository empresaRepository,
            EmpresaMapper empresaMapper
    ) {
        this.empresaRepository = empresaRepository;
        this.empresaMapper = empresaMapper;
    }

    public List<EmpresaResponse> listarTodas() {
        return empresaRepository.findAll()
                .stream()
                .map(empresaMapper::toResponse)
                .toList();
    }

    public Optional<EmpresaResponse> buscarPorId(Long id) {
        return empresaRepository.findById(id)
                .map(empresaMapper::toResponse);
    }

    public EmpresaResponse criar(CreateEmpresaRequest request) {

        if (empresaRepository.existsByCnpj(request.getCnpj())) {
          throw new BusinessException(
        "Já existe uma empresa cadastrada com este CNPJ."
);
        }
        if (empresaRepository.existsByEmail(request.getEmail())) {
    throw new BusinessException(
            "Já existe uma empresa cadastrada com este e-mail."
    );
}

        Empresa empresa = empresaMapper.toEntity(request);

        Empresa empresaSalva = empresaRepository.save(empresa);

        return empresaMapper.toResponse(empresaSalva);
    }

    public boolean excluir(Long id) {

        if (!empresaRepository.existsById(id)) {
            return false;
        }

        empresaRepository.deleteById(id);

        return true;
    }
}