package com.openfree_api.modules.companies.service;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.companies.dto.AddEmpresaUsuarioRequest;
import com.openfree_api.modules.companies.dto.CreateEmpresaRequest;
import com.openfree_api.modules.companies.dto.EmpresaResponse;
import com.openfree_api.modules.companies.dto.EmpresaUsuarioResponse;
import com.openfree_api.modules.companies.entity.CargoEmpresa;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.companies.entity.EmpresaUsuario;
import com.openfree_api.modules.companies.mapper.EmpresaMapper;
import com.openfree_api.modules.companies.mapper.EmpresaUsuarioMapper;
import com.openfree_api.modules.companies.repository.EmpresaRepository;
import com.openfree_api.modules.companies.repository.EmpresaUsuarioRepository;
import com.openfree_api.modules.users.entity.Usuario;
import com.openfree_api.modules.users.repository.UsuarioRepository;
import java.util.List;
import java.util.Optional;
import org.springframework.stereotype.Service;


@Service
public class EmpresaService {
    private EmpresaUsuarioMapper empresaUsuarioMapper;
    private final EmpresaRepository empresaRepository;
    private final EmpresaMapper empresaMapper;
    private final EmpresaUsuarioRepository empresaUsuarioRepository;
    private final UsuarioRepository usuarioRepository;

    public EmpresaService(
            EmpresaRepository empresaRepository,
            EmpresaMapper empresaMapper,
            EmpresaUsuarioRepository empresaUsuarioRepository,
            UsuarioRepository usuarioRepository, EmpresaUsuarioMapper empresaUsuarioMapper
    ) {
        this.empresaRepository = empresaRepository;
        this.empresaMapper = empresaMapper;
        this.empresaUsuarioRepository = empresaUsuarioRepository;
        this.usuarioRepository = usuarioRepository;
        this.empresaUsuarioMapper = empresaUsuarioMapper;
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

        Usuario owner = usuarioRepository
                .findById(request.getOwnerId())
                .orElseThrow(() ->
                        new BusinessException(
                                "Usuário responsável pela empresa não encontrado."
                        )
                );

        Empresa empresa = empresaMapper.toEntity(request);

        Empresa empresaSalva = empresaRepository.save(empresa);

        EmpresaUsuario empresaUsuario = new EmpresaUsuario();

        empresaUsuario.setEmpresa(empresaSalva);
        empresaUsuario.setUsuario(owner);
        empresaUsuario.setCargo(CargoEmpresa.OWNER);
        empresaUsuario.setAtivo(true);
        empresaUsuarioMapper = new EmpresaUsuarioMapper();

        
        empresaUsuarioRepository.save(empresaUsuario);

        return empresaMapper.toResponse(empresaSalva);
    }

    public boolean excluir(Long id) {

        if (!empresaRepository.existsById(id)) {
            return false;
        }

        empresaRepository.deleteById(id);

        return true;
    }

    public List<EmpresaUsuarioResponse> listarMembros(Long empresaId) {

    if (!empresaRepository.existsById(empresaId)) {
        throw new BusinessException("Empresa não encontrada.");
    }

    return empresaUsuarioRepository
            .findByEmpresaId(empresaId)
            .stream()
            .map(empresaUsuarioMapper::toResponse)
            .toList();
}
public EmpresaUsuarioResponse adicionarMembro(
        Long empresaId,
        AddEmpresaUsuarioRequest request
) {

    Empresa empresa = empresaRepository.findById(empresaId)
            .orElseThrow(() ->
                    new BusinessException("Empresa não encontrada.")
            );

    Usuario usuario = usuarioRepository.findById(request.getUsuarioId())
            .orElseThrow(() ->
                    new BusinessException("Usuário não encontrado.")
            );

    if (empresaUsuarioRepository.existsByEmpresaIdAndUsuarioId(
            empresaId,
            request.getUsuarioId()
    )) {
        throw new BusinessException(
                "Este usuário já pertence à empresa."
        );
    }

    EmpresaUsuario empresaUsuario = new EmpresaUsuario();
    empresaUsuario.setEmpresa(empresa);
    empresaUsuario.setUsuario(usuario);
    empresaUsuario.setCargo(request.getCargo());
    empresaUsuario.setAtivo(true);

    EmpresaUsuario salvo =
            empresaUsuarioRepository.save(empresaUsuario);

    return empresaUsuarioMapper.toResponse(salvo);
}


}