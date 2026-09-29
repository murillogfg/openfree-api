package com.openfree_api.modules.users.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.users.dto.CreateUsuarioRequest;
import com.openfree_api.modules.users.dto.UsuarioResponse;
import com.openfree_api.modules.users.entity.Usuario;
import com.openfree_api.modules.users.mapper.UsuarioMapper;
import com.openfree_api.modules.users.repository.UsuarioRepository;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceSegurancaTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    private CreateUsuarioRequest request(String email) {
        CreateUsuarioRequest request = new CreateUsuarioRequest();
        request.setNome("Murillo");
        request.setEmail(email);
        request.setSenha("123456");
        request.setAceitouTermos(true);
        return request;
    }

    @Test
    void deveSalvarEmailEmMinusculoESemEspacos() {

        when(usuarioRepository.existsByEmailIgnoreCase("murillo@gmail.com"))
                .thenReturn(false);

        ArgumentCaptor<CreateUsuarioRequest> captor =
                ArgumentCaptor.forClass(CreateUsuarioRequest.class);

        when(usuarioMapper.toEntity(captor.capture()))
                .thenReturn(new Usuario());
        when(usuarioRepository.save(any(Usuario.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        when(usuarioMapper.toResponse(any(Usuario.class)))
                .thenReturn(new UsuarioResponse());

        usuarioService.criar(request("  Murillo@Gmail.com "));

        assertEquals("murillo@gmail.com", captor.getValue().getEmail());
    }

    @Test
    void naoDeveCadastrarEmailRepetidoMesmoComMaiusculas() {

        when(usuarioRepository.existsByEmailIgnoreCase("murillo@gmail.com"))
                .thenReturn(true);

        BusinessException erro = assertThrows(
                BusinessException.class,
                () -> usuarioService.criar(request("MURILLO@gmail.com"))
        );

        assertEquals("Este e-mail já está cadastrado.", erro.getMessage());
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void perfilPublicoNaoExpoeEmailNemTelefone() {

        Usuario usuario = new Usuario();
        usuario.setId(7L);

        UsuarioResponse completo = new UsuarioResponse();
        completo.setId(7L);
        completo.setNome("Murillo");
        completo.setEmail("murillo@gmail.com");
        completo.setTelefone("11999999999");

        when(usuarioRepository.findById(7L))
                .thenReturn(Optional.of(usuario));
        when(usuarioMapper.toResponse(usuario))
                .thenReturn(completo);

        UsuarioResponse publico = usuarioService.buscarPerfilPublico(7L);

        assertEquals("Murillo", publico.getNome());
        assertNull(publico.getEmail());
        assertNull(publico.getTelefone());
    }
}
