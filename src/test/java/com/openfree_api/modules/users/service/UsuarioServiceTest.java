package com.openfree_api.modules.users.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.users.dto.CreateUsuarioRequest;
import com.openfree_api.modules.users.dto.UsuarioResponse;
import com.openfree_api.modules.users.entity.Usuario;
import com.openfree_api.modules.users.enums.Role;
import com.openfree_api.modules.users.mapper.UsuarioMapper;
import com.openfree_api.modules.users.repository.UsuarioRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.crypto.password.PasswordEncoder;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.users.entity.Usuario;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private UsuarioMapper usuarioMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    void deveCriarUsuarioComSucesso() {
        CreateUsuarioRequest request = new CreateUsuarioRequest();
        request.setNome("Murillo");
        request.setEmail("murillo@email.com");
        request.setSenha("123456");
        request.setAceitouTermos(true);

        Usuario usuario = new Usuario();
        usuario.setNome("Murillo");
        usuario.setEmail("murillo@email.com");

        Usuario usuarioSalvo = new Usuario();
        usuarioSalvo.setId(1L);
        usuarioSalvo.setNome("Murillo");
        usuarioSalvo.setEmail("murillo@email.com");
        usuarioSalvo.setRole(Role.FREELANCER);

        UsuarioResponse responseEsperada = new UsuarioResponse();
        responseEsperada.setId(1L);
        responseEsperada.setNome("Murillo");
        responseEsperada.setEmail("murillo@email.com");

        when(usuarioMapper.toEntity(request)).thenReturn(usuario);
        when(passwordEncoder.encode("123456")).thenReturn("senha-criptografada");
        when(usuarioRepository.save(usuario)).thenReturn(usuarioSalvo);
        when(usuarioMapper.toResponse(usuarioSalvo)).thenReturn(responseEsperada);

        UsuarioResponse response = usuarioService.criar(request);

        assertNotNull(response);
        assertEquals(1L, response.getId());
        assertEquals("Murillo", response.getNome());
        assertEquals("murillo@email.com", response.getEmail());
        assertEquals("senha-criptografada", usuario.getSenha());
        assertEquals(Role.FREELANCER, usuario.getRole());

        verify(passwordEncoder).encode("123456");
        verify(usuarioRepository).save(usuario);
        verify(usuarioMapper).toResponse(usuarioSalvo);
    }

  @Test
void deveListarTodosOsUsuarios() {

    Usuario usuario = new Usuario();
    usuario.setId(1L);
    usuario.setNome("Murillo");
    usuario.setEmail("murillo@email.com");
    usuario.setRole(Role.FREELANCER);

    UsuarioResponse response =
            new UsuarioResponse();

    response.setId(1L);
    response.setNome("Murillo");
    response.setEmail("murillo@email.com");

    when(
            usuarioRepository.findAll()
    ).thenReturn(
            List.of(usuario)
    );

    when(
            usuarioMapper.toResponse(usuario)
    ).thenReturn(response);

    List<UsuarioResponse> resultado =
            usuarioService.listarTodos();

    assertNotNull(resultado);

    assertEquals(
            1,
            resultado.size()
    );

    assertEquals(
            "Murillo",
            resultado.get(0).getNome()
    );

    assertEquals(
            "murillo@email.com",
            resultado.get(0).getEmail()
    );

    verify(usuarioRepository)
            .findAll();

    verify(usuarioMapper)
            .toResponse(usuario);
}

@Test
void deveBuscarUsuarioPorIdComSucesso() {

    Long id = 1L;

    Usuario usuario = new Usuario();
    usuario.setId(id);
    usuario.setNome("Murillo");
    usuario.setEmail("murillo@email.com");

    UsuarioResponse response =
            new UsuarioResponse();

    response.setId(id);
    response.setNome("Murillo");
    response.setEmail("murillo@email.com");

    when(
            usuarioRepository.findById(id)
    ).thenReturn(Optional.of(usuario));

    when(
            usuarioMapper.toResponse(usuario)
    ).thenReturn(response);

    Optional<UsuarioResponse> resultado =
            usuarioService.buscarPorId(id);

    assertNotNull(resultado);

    assertEquals(
            true,
            resultado.isPresent()
    );

    assertEquals(
            id,
            resultado.get().getId()
    );

    assertEquals(
            "Murillo",
            resultado.get().getNome()
    );

    verify(usuarioRepository)
            .findById(id);

    verify(usuarioMapper)
            .toResponse(usuario);
}
@Test
void deveRetornarOptionalVazioQuandoUsuarioNaoExistir() {

    Long id = 999L;

    when(
            usuarioRepository.findById(id)
    ).thenReturn(Optional.empty());

    Optional<UsuarioResponse> resultado =
            usuarioService.buscarPorId(id);

    assertNotNull(resultado);

    assertEquals(
            true,
            resultado.isEmpty()
    );

    verify(usuarioRepository)
            .findById(id);

    verify(
            usuarioMapper,
            never()
    ).toResponse(any(Usuario.class));
}
@Test
void deveExcluirUsuarioComSucesso() {

    Long id = 1L;

    when(
            usuarioRepository.existsById(id)
    ).thenReturn(true);

    boolean resultado =
            usuarioService.excluir(id);

    assertEquals(
            true,
            resultado
    );

    verify(usuarioRepository)
            .existsById(id);

    verify(usuarioRepository)
            .deleteById(id);
}
@Test
void deveRetornarFalseQuandoUsuarioNaoExistirAoExcluir() {

    Long id = 999L;

    when(
            usuarioRepository.existsById(id)
    ).thenReturn(false);

    boolean resultado =
            usuarioService.excluir(id);

    assertEquals(
            false,
            resultado
    );

    verify(usuarioRepository)
            .existsById(id);

    verify(
            usuarioRepository,
            never()
    ).deleteById(anyLong());
}


@Test
void deveBloquearCriacaoDeUsuarioSemAceitarTermos() {

    CreateUsuarioRequest request =
            new CreateUsuarioRequest();

    request.setNome("Teste");
    request.setEmail("teste@email.com");
    request.setSenha("123456");
    request.setTelefone("11999999999");

    request.setAceitouTermos(false);

    BusinessException exception =
            assertThrows(
                    BusinessException.class,
                    () ->
                            usuarioService.criar(
                                    request
                            )
            );

    assertEquals(
            "É necessário aceitar os Termos de Uso e a Política de Privacidade.",
            exception.getMessage()
    );

    verify(
            usuarioRepository,
            never()
    ).save(
            any(Usuario.class)
    );
}

}
