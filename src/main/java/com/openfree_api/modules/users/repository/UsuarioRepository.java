package com.openfree_api.modules.users.repository;

import com.openfree_api.modules.users.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepository extends JpaRepository<Usuario, Long> {

}