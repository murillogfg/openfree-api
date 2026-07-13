package com.openfree_api.modules.users.service;

import com.openfree_api.modules.users.dto.CreateUsuarioRequest;
import com.openfree_api.modules.users.dto.UsuarioResponse;
import com.openfree_api.modules.users.entity.Usuario;
import com.openfree_api.modules.users.repository.UsuarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    public List<Usuario> listarTodos() {
        return usuarioRepository.findAll();
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return usuarioRepository.findById(id);
    }

    public <S> Usuario criar1(CreateUsuarioRequest request) {
        // Map CreateUsuarioRequest to Usuario entity. Adjust mapping as needed.
        Usuario usuario = new Usuario();
        // Example mappings (uncomment and adjust to real fields):
        // usuario.setName(request.getName());
        // usuario.setEmail(request.getEmail());
        return usuarioRepository.save(usuario);
    }

    public boolean excluir(Long id) {
        usuarioRepository.deleteById(id);
        return false;
    }

    public UsuarioResponse criar(CreateUsuarioRequest request) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'criar'");
    }
}