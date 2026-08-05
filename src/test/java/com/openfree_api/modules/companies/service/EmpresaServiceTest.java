package com.openfree_api.modules.companies.service;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.candidaturas.entity.StatusCandidatura;
import com.openfree_api.modules.candidaturas.repository.CandidaturaRepository;
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
import com.openfree_api.modules.dashboard.dto.DashboardEmpresaResponse;
import com.openfree_api.modules.jobs.entity.StatusVaga;
import com.openfree_api.modules.jobs.repository.VagaRepository;
import com.openfree_api.modules.users.entity.Usuario;
import com.openfree_api.modules.users.enums.Role;
import com.openfree_api.modules.users.repository.UsuarioRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



@ExtendWith(MockitoExtension.class)
class EmpresaServiceTest {

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private EmpresaMapper empresaMapper;

    @Mock
    private EmpresaUsuarioRepository empresaUsuarioRepository;

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private EmpresaUsuarioMapper empresaUsuarioMapper;

    @Mock
    private VagaRepository vagaRepository;

    @Mock
    private CandidaturaRepository candidaturaRepository;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private EmpresaService empresaService;

    @Test
    void deveCriarEmpresaComSucesso() {

        CreateEmpresaRequest request =
                new CreateEmpresaRequest();

        request.setCnpj("12345678000180");
        request.setEmail("contato@openfree.com");
        request.setNomeFantasia("OpenFree");
        request.setRazaoSocial(
                "OpenFree Tecnologia LTDA"
        );

        Usuario owner = new Usuario();
        owner.setId(10L);
        owner.setNome("Murillo");
        owner.setEmail("murillo@email.com");
        owner.setRole(Role.FREELANCER);

        Empresa empresaMapeada = new Empresa();
        empresaMapeada.setNomeFantasia(
                request.getNomeFantasia()
        );
        empresaMapeada.setRazaoSocial(
                request.getRazaoSocial()
        );
        empresaMapeada.setCnpj(
                request.getCnpj()
        );
        empresaMapeada.setEmail(
                request.getEmail()
        );

        Empresa empresaSalva = new Empresa();
        empresaSalva.setId(1L);
        empresaSalva.setNomeFantasia(
                request.getNomeFantasia()
        );
        empresaSalva.setRazaoSocial(
                request.getRazaoSocial()
        );
        empresaSalva.setCnpj(
                request.getCnpj()
        );
        empresaSalva.setEmail(
                request.getEmail()
        );
        empresaSalva.setUsuario(owner);

        EmpresaResponse responseEsperada =
                new EmpresaResponse();

        responseEsperada.setId(1L);
        responseEsperada.setNomeFantasia(
                "OpenFree"
        );

        when(
                empresaRepository.existsByCnpj(
                        request.getCnpj()
                )
        ).thenReturn(false);

        when(
                empresaRepository.existsByEmail(
                        request.getEmail()
                )
        ).thenReturn(false);

        when(
                authentication.getName()
        ).thenReturn("murillo@email.com");

        when(
                usuarioRepository.findByEmail(
                        "murillo@email.com"
                )
        ).thenReturn(Optional.of(owner));

        when(
                empresaMapper.toEntity(request)
        ).thenReturn(empresaMapeada);

        when(
                empresaRepository.save(
                        empresaMapeada
                )
        ).thenReturn(empresaSalva);

        when(
                empresaMapper.toResponse(
                        empresaSalva
                )
        ).thenReturn(responseEsperada);

        EmpresaResponse response =
                empresaService.criar(
                        request,
                        authentication
                );

        assertNotNull(response);

        assertEquals(
                1L,
                response.getId()
        );

        assertEquals(
                "OpenFree",
                response.getNomeFantasia()
        );

        assertSame(
                owner,
                empresaMapeada.getUsuario()
        );

        assertEquals(
                Role.EMPRESA,
                owner.getRole()
        );

        ArgumentCaptor<EmpresaUsuario> captor =
                ArgumentCaptor.forClass(
                        EmpresaUsuario.class
                );

        verify(empresaUsuarioRepository)
                .save(captor.capture());

        EmpresaUsuario vinculo =
                captor.getValue();

        assertSame(
                empresaSalva,
                vinculo.getEmpresa()
        );

        assertSame(
                owner,
                vinculo.getUsuario()
        );

        assertEquals(
                CargoEmpresa.OWNER,
                vinculo.getCargo()
        );

        assertEquals(
                true,
                vinculo.getAtivo()
        );

        verify(empresaRepository)
                .save(empresaMapeada);

        verify(usuarioRepository)
                .save(owner);

        verify(empresaMapper)
                .toResponse(empresaSalva);
    }

    @Test
void deveLancarExcecaoQuandoCnpjJaExistir() {

    CreateEmpresaRequest request =
            new CreateEmpresaRequest();

    request.setCnpj("12345678000180");
    request.setEmail("contato@openfree.com");

    when(
            empresaRepository.existsByCnpj(
                    request.getCnpj()
            )
    ).thenReturn(true);

    BusinessException exception =
            assertThrows(
                    BusinessException.class,
                    () -> empresaService.criar(
                            request,
                            authentication
                    )
            );

    assertEquals(
            "Já existe uma empresa cadastrada com este CNPJ.",
            exception.getMessage()
    );

    verify(empresaRepository)
            .existsByCnpj(
                    request.getCnpj()
            );

    verify(
            empresaRepository,
            never()
    ).save(any());

    verify(
            usuarioRepository,
            never()
    ).findByEmail(any());

    verify(
            empresaUsuarioRepository,
            never()
    ).save(any());
}

@Test
void deveLancarExcecaoQuandoEmailJaExistir() {

    CreateEmpresaRequest request =
            new CreateEmpresaRequest();

    request.setCnpj("12345678000180");
    request.setEmail("contato@openfree.com");

    when(
            empresaRepository.existsByCnpj(
                    request.getCnpj()
            )
    ).thenReturn(false);

    when(
            empresaRepository.existsByEmail(
                    request.getEmail()
            )
    ).thenReturn(true);

    BusinessException exception =
            assertThrows(
                    BusinessException.class,
                    () -> empresaService.criar(
                            request,
                            authentication
                    )
            );

    assertEquals(
            "Já existe uma empresa cadastrada com este e-mail.",
            exception.getMessage()
    );

    verify(empresaRepository)
            .existsByCnpj(
                    request.getCnpj()
            );

    verify(empresaRepository)
            .existsByEmail(
                    request.getEmail()
            );

    verify(
            usuarioRepository,
            never()
    ).findByEmail(any());

    verify(
            empresaRepository,
            never()
    ).save(any());

    verify(
            empresaUsuarioRepository,
            never()
    ).save(any());
}

@Test
void deveLancarExcecaoQuandoUsuarioAutenticadoNaoExistir() {

    CreateEmpresaRequest request =
            new CreateEmpresaRequest();

    request.setCnpj("12345678000180");
    request.setEmail("contato@openfree.com");

    when(
            empresaRepository.existsByCnpj(
                    request.getCnpj()
            )
    ).thenReturn(false);

    when(
            empresaRepository.existsByEmail(
                    request.getEmail()
            )
    ).thenReturn(false);

    when(
            authentication.getName()
    ).thenReturn("murillo@email.com");

    when(
            usuarioRepository.findByEmail(
                    "murillo@email.com"
            )
    ).thenReturn(Optional.empty());

    BusinessException exception =
            assertThrows(
                    BusinessException.class,
                    () -> empresaService.criar(
                            request,
                            authentication
                    )
            );

    assertEquals(
            "Usuário autenticado não encontrado.",
            exception.getMessage()
    );

    verify(
            empresaRepository,
            never()
    ).save(any(Empresa.class));

    verify(
            empresaUsuarioRepository,
            never()
    ).save(any(EmpresaUsuario.class));

    verify(
            usuarioRepository,
            never()
    ).save(any(Usuario.class));
}

@Test
void deveListarMembrosDaEmpresa() {

    Long empresaId = 1L;

    EmpresaUsuario membro =
            new EmpresaUsuario();

    EmpresaUsuarioResponse response =
            new EmpresaUsuarioResponse();

    response.setCargo(
            CargoEmpresa.OWNER
    );

    when(
            empresaRepository.existsById(
                    empresaId
            )
    ).thenReturn(true);

    when(
            empresaUsuarioRepository.findByEmpresaId(
                    empresaId
            )
    ).thenReturn(
            List.of(membro)
    );

    when(
            empresaUsuarioMapper.toResponse(
                    membro
            )
    ).thenReturn(response);

    List<EmpresaUsuarioResponse> resultado =
            empresaService.listarMembros(
                    empresaId
            );

    assertEquals(
            1,
            resultado.size()
    );

    assertEquals(
            CargoEmpresa.OWNER,
            resultado.get(0).getCargo()
    );

    verify(empresaRepository)
            .existsById(empresaId);

    verify(empresaUsuarioRepository)
            .findByEmpresaId(empresaId);

    verify(empresaUsuarioMapper)
            .toResponse(membro);
}
@Test
void deveLancarExcecaoQuandoEmpresaNaoExistirAoListarMembros() {

    Long empresaId = 999L;

    when(
            empresaRepository.existsById(
                    empresaId
            )
    ).thenReturn(false);

    BusinessException exception =
            assertThrows(
                    BusinessException.class,
                    () -> empresaService.listarMembros(
                            empresaId
                    )
            );

    assertEquals(
            "Empresa não encontrada.",
            exception.getMessage()
    );

    verify(empresaRepository)
            .existsById(empresaId);

    verify(
            empresaUsuarioRepository,
            never()
    ).findByEmpresaId(anyLong());

    verify(
            empresaUsuarioMapper,
            never()
    ).toResponse(any(EmpresaUsuario.class));
}
@Test
void deveAdicionarMembroComSucesso() {

    Long empresaId = 1L;
    Long usuarioId = 20L;

    Empresa empresa = new Empresa();
    empresa.setId(empresaId);
    empresa.setNomeFantasia("OpenFree");

    Usuario usuario = new Usuario();
    usuario.setId(usuarioId);
    usuario.setNome("Carlos");
    usuario.setEmail("carlos@email.com");

    AddEmpresaUsuarioRequest request =
            new AddEmpresaUsuarioRequest();

    request.setUsuarioId(usuarioId);
    request.setCargo(CargoEmpresa.OWNER);

    EmpresaUsuario empresaUsuarioSalvo =
            new EmpresaUsuario();

    empresaUsuarioSalvo.setEmpresa(empresa);
    empresaUsuarioSalvo.setUsuario(usuario);
    empresaUsuarioSalvo.setCargo(
            CargoEmpresa.OWNER
    );
    empresaUsuarioSalvo.setAtivo(true);

    EmpresaUsuarioResponse responseEsperada =
            new EmpresaUsuarioResponse();

    responseEsperada.setCargo(
            CargoEmpresa.OWNER
    );

    when(
            empresaRepository.findById(empresaId)
    ).thenReturn(Optional.of(empresa));

    when(
            usuarioRepository.findById(usuarioId)
    ).thenReturn(Optional.of(usuario));

    when(
            empresaUsuarioRepository
                    .existsByEmpresaIdAndUsuarioId(
                            empresaId,
                            usuarioId
                    )
    ).thenReturn(false);

    when(
            empresaUsuarioRepository.save(
                    any(EmpresaUsuario.class)
            )
    ).thenReturn(empresaUsuarioSalvo);

    when(
            empresaUsuarioMapper.toResponse(
                    empresaUsuarioSalvo
            )
    ).thenReturn(responseEsperada);

    EmpresaUsuarioResponse response =
            empresaService.adicionarMembro(
                    empresaId,
                    request
            );

    assertNotNull(response);

    assertEquals(
            CargoEmpresa.OWNER,
            response.getCargo()
    );

    ArgumentCaptor<EmpresaUsuario> captor =
            ArgumentCaptor.forClass(
                    EmpresaUsuario.class
            );

    verify(empresaUsuarioRepository)
            .save(captor.capture());

    EmpresaUsuario membroCriado =
            captor.getValue();

    assertSame(
            empresa,
            membroCriado.getEmpresa()
    );

    assertSame(
            usuario,
            membroCriado.getUsuario()
    );

    assertEquals(
            CargoEmpresa.OWNER,
            membroCriado.getCargo()
    );

    assertEquals(
            true,
            membroCriado.getAtivo()
    );

    verify(empresaRepository)
            .findById(empresaId);

    verify(usuarioRepository)
            .findById(usuarioId);

    verify(empresaUsuarioRepository)
            .existsByEmpresaIdAndUsuarioId(
                    empresaId,
                    usuarioId
            );

    verify(empresaUsuarioMapper)
            .toResponse(empresaUsuarioSalvo);
}
@Test
void deveLancarExcecaoQuandoEmpresaNaoExistirAoAdicionarMembro() {

    Long empresaId = 999L;

    AddEmpresaUsuarioRequest request =
            new AddEmpresaUsuarioRequest();

    request.setUsuarioId(20L);
    request.setCargo(CargoEmpresa.OWNER);

    when(
            empresaRepository.findById(empresaId)
    ).thenReturn(Optional.empty());

    BusinessException exception =
            assertThrows(
                    BusinessException.class,
                    () -> empresaService.adicionarMembro(
                            empresaId,
                            request
                    )
            );

    assertEquals(
            "Empresa não encontrada.",
            exception.getMessage()
    );

    verify(empresaRepository)
            .findById(empresaId);

    verify(
            usuarioRepository,
            never()
    ).findById(anyLong());

    verify(
            empresaUsuarioRepository,
            never()
    ).existsByEmpresaIdAndUsuarioId(
            anyLong(),
            anyLong()
    );

    verify(
            empresaUsuarioRepository,
            never()
    ).save(any(EmpresaUsuario.class));
}

@Test
void deveLancarExcecaoQuandoUsuarioNaoExistirAoAdicionarMembro() {

    Long empresaId = 1L;
    Long usuarioId = 999L;

    Empresa empresa = new Empresa();
    empresa.setId(empresaId);

    AddEmpresaUsuarioRequest request =
            new AddEmpresaUsuarioRequest();

    request.setUsuarioId(usuarioId);
    request.setCargo(CargoEmpresa.OWNER);

    when(
            empresaRepository.findById(empresaId)
    ).thenReturn(Optional.of(empresa));

    when(
            usuarioRepository.findById(usuarioId)
    ).thenReturn(Optional.empty());

    BusinessException exception =
            assertThrows(
                    BusinessException.class,
                    () -> empresaService.adicionarMembro(
                            empresaId,
                            request
                    )
            );

    assertEquals(
            "Usuário não encontrado.",
            exception.getMessage()
    );

    verify(empresaRepository)
            .findById(empresaId);

    verify(usuarioRepository)
            .findById(usuarioId);

    verify(
            empresaUsuarioRepository,
            never()
    ).existsByEmpresaIdAndUsuarioId(
            anyLong(),
            anyLong()
    );

    verify(
            empresaUsuarioRepository,
            never()
    ).save(any(EmpresaUsuario.class));
}

@Test
void deveLancarExcecaoQuandoUsuarioJaPertencerAEmpresa() {

    Long empresaId = 1L;
    Long usuarioId = 20L;

    Empresa empresa = new Empresa();
    empresa.setId(empresaId);

    Usuario usuario = new Usuario();
    usuario.setId(usuarioId);

    AddEmpresaUsuarioRequest request =
            new AddEmpresaUsuarioRequest();

    request.setUsuarioId(usuarioId);
    request.setCargo(CargoEmpresa.OWNER);

    when(
            empresaRepository.findById(empresaId)
    ).thenReturn(Optional.of(empresa));

    when(
            usuarioRepository.findById(usuarioId)
    ).thenReturn(Optional.of(usuario));

    when(
            empresaUsuarioRepository
                    .existsByEmpresaIdAndUsuarioId(
                            empresaId,
                            usuarioId
                    )
    ).thenReturn(true);

    BusinessException exception =
            assertThrows(
                    BusinessException.class,
                    () -> empresaService.adicionarMembro(
                            empresaId,
                            request
                    )
            );

    assertEquals(
            "Este usuário já pertence à empresa.",
            exception.getMessage()
    );

    verify(
            empresaUsuarioRepository,
            never()
    ).save(any(EmpresaUsuario.class));
}

@Test
void deveGerarDashboardDaEmpresaComSucesso() {

    Long empresaId = 1L;

    when(
            empresaRepository.existsById(empresaId)
    ).thenReturn(true);

    when(
            vagaRepository.countByEmpresaId(empresaId)
    ).thenReturn(8L);

    when(
            vagaRepository.countByEmpresaIdAndStatus(
                    empresaId,
                    StatusVaga.PUBLICADA
            )
    ).thenReturn(5L);

    when(
            vagaRepository.countByEmpresaIdAndStatus(
                    empresaId,
                    StatusVaga.FINALIZADA
            )
    ).thenReturn(3L);

    when(
            candidaturaRepository.countByVagaEmpresaId(
                    empresaId
            )
    ).thenReturn(20L);

    when(
            candidaturaRepository.countByVagaEmpresaIdAndStatus(
                    empresaId,
                    StatusCandidatura.ACEITA
            )
    ).thenReturn(4L);

    DashboardEmpresaResponse response =
            empresaService.dashboard(empresaId);

    assertNotNull(response);

    assertEquals(
            8L,
            response.getVagasPublicadas()
    );

    assertEquals(
            5L,
            response.getVagasAbertas()
    );

    assertEquals(
            3L,
            response.getVagasFinalizadas()
    );

    assertEquals(
            20L,
            response.getCandidaturasRecebidas()
    );

    assertEquals(
            4L,
            response.getProfissionaisContratados()
    );

    verify(empresaRepository)
            .existsById(empresaId);

    verify(vagaRepository)
            .countByEmpresaId(empresaId);

    verify(vagaRepository)
            .countByEmpresaIdAndStatus(
                    empresaId,
                    StatusVaga.PUBLICADA
            );

    verify(vagaRepository)
            .countByEmpresaIdAndStatus(
                    empresaId,
                    StatusVaga.FINALIZADA
            );

    verify(candidaturaRepository)
            .countByVagaEmpresaId(empresaId);

    verify(candidaturaRepository)
            .countByVagaEmpresaIdAndStatus(
                    empresaId,
                    StatusCandidatura.ACEITA
            );
}

@Test
void deveLancarExcecaoQuandoEmpresaNaoExistirAoGerarDashboard() {

    Long empresaId = 999L;

    when(
            empresaRepository.existsById(empresaId)
    ).thenReturn(false);

    BusinessException exception =
            assertThrows(
                    BusinessException.class,
                    () -> empresaService.dashboard(
                            empresaId
                    )
            );

    assertEquals(
            "Empresa não encontrada.",
            exception.getMessage()
    );

    verify(empresaRepository)
            .existsById(empresaId);

    verify(
            vagaRepository,
            never()
    ).countByEmpresaId(anyLong());

    verify(
            vagaRepository,
            never()
    ).countByEmpresaIdAndStatus(
            anyLong(),
            any(StatusVaga.class)
    );

    verify(
            candidaturaRepository,
            never()
    ).countByVagaEmpresaId(anyLong());

    verify(
            candidaturaRepository,
            never()
    ).countByVagaEmpresaIdAndStatus(
            anyLong(),
            any(StatusCandidatura.class)
    );
}
@Test
void deveListarTodasAsEmpresas() {

    Empresa empresa = new Empresa();
    empresa.setId(1L);
    empresa.setNomeFantasia("OpenFree");

    EmpresaResponse response =
            new EmpresaResponse();

    response.setId(1L);
    response.setNomeFantasia("OpenFree");

    when(
            empresaRepository.findAll()
    ).thenReturn(List.of(empresa));

    when(
            empresaMapper.toResponse(empresa)
    ).thenReturn(response);

    List<EmpresaResponse> resultado =
            empresaService.listarTodas();

    assertEquals(
            1,
            resultado.size()
    );

    assertEquals(
            "OpenFree",
            resultado.get(0).getNomeFantasia()
    );

    verify(empresaRepository)
            .findAll();

    verify(empresaMapper)
            .toResponse(empresa);
}
@Test
void deveBuscarEmpresaPorIdComSucesso() {

    Long empresaId = 1L;

    Empresa empresa = new Empresa();
    empresa.setId(empresaId);
    empresa.setNomeFantasia("OpenFree");

    EmpresaResponse response =
            new EmpresaResponse();

    response.setId(empresaId);
    response.setNomeFantasia("OpenFree");

    when(
            empresaRepository.findById(empresaId)
    ).thenReturn(Optional.of(empresa));

    when(
            empresaMapper.toResponse(empresa)
    ).thenReturn(response);

    Optional<EmpresaResponse> resultado =
            empresaService.buscarPorId(empresaId);

    assertEquals(
            true,
            resultado.isPresent()
    );

    assertEquals(
            empresaId,
            resultado.get().getId()
    );

    assertEquals(
            "OpenFree",
            resultado.get().getNomeFantasia()
    );

    verify(empresaRepository)
            .findById(empresaId);

    verify(empresaMapper)
            .toResponse(empresa);
}

@Test
void deveRetornarOptionalVazioQuandoEmpresaNaoExistir() {

    Long empresaId = 999L;

    when(
            empresaRepository.findById(empresaId)
    ).thenReturn(Optional.empty());

    Optional<EmpresaResponse> resultado =
            empresaService.buscarPorId(empresaId);

    assertEquals(
            true,
            resultado.isEmpty()
    );

    verify(empresaRepository)
            .findById(empresaId);

    verify(
            empresaMapper,
            never()
    ).toResponse(any(Empresa.class));
}
@Test
void deveExcluirEmpresaComSucesso() {

    Long empresaId = 1L;

    when(
            empresaRepository.existsById(empresaId)
    ).thenReturn(true);

    boolean resultado =
            empresaService.excluir(empresaId);

    assertEquals(
            true,
            resultado
    );

    verify(empresaRepository)
            .existsById(empresaId);

    verify(empresaRepository)
            .deleteById(empresaId);
}
@Test
void deveRetornarFalseQuandoEmpresaNaoExistirAoExcluir() {

    Long empresaId = 999L;

    when(
            empresaRepository.existsById(empresaId)
    ).thenReturn(false);

    boolean resultado =
            empresaService.excluir(empresaId);

    assertEquals(
            false,
            resultado
    );

    verify(empresaRepository)
            .existsById(empresaId);

    verify(
            empresaRepository,
            never()
    ).deleteById(anyLong());
}

}