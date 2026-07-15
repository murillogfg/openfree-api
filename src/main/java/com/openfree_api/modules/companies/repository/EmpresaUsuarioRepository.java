package com.openfree_api.modules.companies.repository;

import com.openfree_api.modules.companies.entity.CargoEmpresa;
import com.openfree_api.modules.companies.entity.EmpresaUsuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EmpresaUsuarioRepository
        extends JpaRepository<EmpresaUsuario, Long> {

    List<EmpresaUsuario> findByEmpresaId(Long empresaId);

    List<EmpresaUsuario> findByUsuarioId(Long usuarioId);

    Optional<EmpresaUsuario> findByEmpresaIdAndUsuarioId(
            Long empresaId,
            Long usuarioId
    );

    boolean existsByEmpresaIdAndUsuarioId(
            Long empresaId,
            Long usuarioId
    );

    boolean existsByEmpresaIdAndCargo(
            Long empresaId,
            CargoEmpresa cargo
    );
}