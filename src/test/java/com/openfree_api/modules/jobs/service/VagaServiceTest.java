package com.openfree_api.modules.jobs.service;

import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.auth.service.EmpresaAuthService;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.companies.repository.EmpresaRepository;
import com.openfree_api.modules.jobs.dto.CreateVagaRequest;
import com.openfree_api.modules.jobs.dto.VagaResponse;
import com.openfree_api.modules.jobs.entity.StatusVaga;
import com.openfree_api.modules.jobs.entity.Vaga;
import com.openfree_api.modules.jobs.mapper.VagaMapper;
import com.openfree_api.modules.jobs.repository.VagaRepository;

import org.springframework.data.domain.Sort;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openfree_api.common.response.PageResponse;
import com.openfree_api.modules.jobs.dto.JobFilterRequest;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import static org.mockito.ArgumentMatchers.eq;




@ExtendWith(MockitoExtension.class)
class VagaServiceTest {

    @Mock
    private VagaRepository vagaRepository;

    @Mock
    private EmpresaRepository empresaRepository;

    @Mock
    private EmpresaAuthService empresaAuthService;

    @Mock
    private VagaMapper vagaMapper;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private VagaService vagaService;

    @Test
    void deveCriarVagaComSucesso() {

        Empresa empresa = new Empresa();
        empresa.setId(1L);
        empresa.setNomeFantasia("OpenFree");
        empresa.setAtiva(true);

        CreateVagaRequest request =
                new CreateVagaRequest();

        request.setTitulo("Cozinheiro para evento");
        request.setDescricao("Trabalho em evento corporativo.");
        request.setRequisitos("Experiência em cozinha.");
        request.setCidade("São Paulo");
        request.setEstado("SP");
        request.setValor(new BigDecimal("250.00"));
        request.setQuantidadePessoas(2);
        request.setDataServico(LocalDate.of(2099, 8, 20));
        request.setHorarioInicio(LocalTime.of(18, 0));
        request.setHorarioFim(LocalTime.of(23, 0));

        Vaga vagaMapeada = new Vaga();
        vagaMapeada.setTitulo(request.getTitulo());

        Vaga vagaSalva = new Vaga();
        vagaSalva.setId(10L);
        vagaSalva.setTitulo(request.getTitulo());
        vagaSalva.setEmpresa(empresa);

        VagaResponse responseEsperada =
                new VagaResponse();

        responseEsperada.setId(10L);
        responseEsperada.setTitulo(
                "Cozinheiro para evento"
        );

        when(
                empresaAuthService.getEmpresaLogada(
                        authentication
                )
        ).thenReturn(empresa);

        when(
                vagaMapper.toEntity(request)
        ).thenReturn(vagaMapeada);

        when(
                vagaRepository.save(
                        any(Vaga.class)
                )
        ).thenReturn(vagaSalva);

        when(
                vagaMapper.toResponse(vagaSalva)
        ).thenReturn(responseEsperada);

        VagaResponse response =
                vagaService.criar(
                        request,
                        authentication
                );

        assertNotNull(response);

        assertEquals(
                10L,
                response.getId()
        );

        assertEquals(
                "Cozinheiro para evento",
                response.getTitulo()
        );

        ArgumentCaptor<Vaga> captor =
                ArgumentCaptor.forClass(Vaga.class);

        verify(vagaRepository)
                .save(captor.capture());

        Vaga vagaEnviadaParaSalvar =
                captor.getValue();

        assertSame(
                empresa,
                vagaEnviadaParaSalvar.getEmpresa()
        );

        assertEquals(
                "Cozinheiro para evento",
                vagaEnviadaParaSalvar.getTitulo()
        );

        verify(empresaAuthService)
                .getEmpresaLogada(authentication);

        verify(vagaMapper)
                .toEntity(request);

        verify(vagaMapper)
                .toResponse(vagaSalva);
    }

    @Test
void deveLancarExcecaoQuandoEmpresaEstiverInativa() {

    Empresa empresa = new Empresa();
    empresa.setId(1L);
    empresa.setNomeFantasia("OpenFree");
    empresa.setAtiva(false);

    CreateVagaRequest request =
            new CreateVagaRequest();

    request.setHorarioInicio(
            LocalTime.of(18, 0)
    );

    request.setHorarioFim(
            LocalTime.of(23, 0)
    );

    when(
            empresaAuthService.getEmpresaLogada(authentication)
    ).thenReturn(empresa);

    BusinessException exception =
            assertThrows(
                    BusinessException.class,
                    () -> vagaService.criar(
                            request,
                            authentication
                    )
            );

    assertEquals(
            "A empresa está inativa e não pode criar vagas.",
            exception.getMessage()
    );

    verify(vagaRepository, never())
            .save(any(Vaga.class));
}

@Test
void deveLancarExcecaoQuandoHorarioForInvalido() {

    Empresa empresa = new Empresa();
    empresa.setId(1L);
    empresa.setNomeFantasia("OpenFree");
    empresa.setAtiva(true);

    CreateVagaRequest request =
            new CreateVagaRequest();

    request.setHorarioInicio(
            LocalTime.of(18, 0)
    );

    request.setHorarioFim(
            LocalTime.of(17, 0)
    );

    when(
            empresaAuthService.getEmpresaLogada(authentication)
    ).thenReturn(empresa);

    BusinessException exception =
            assertThrows(
                    BusinessException.class,
                    () -> vagaService.criar(
                            request,
                            authentication
                    )
            );

    assertEquals(
            "O horário de término deve ser posterior ao horário de início.",
            exception.getMessage()
    );

    verify(vagaRepository, never())
            .save(any(Vaga.class));

    verify(vagaMapper, never())
            .toEntity(any(CreateVagaRequest.class));
}
@Test
void deveBuscarVagaPorIdComSucesso() {

    Long vagaId = 10L;

    Vaga vaga = new Vaga();
    vaga.setId(vagaId);
    vaga.setTitulo("Cozinheiro para evento");

    VagaResponse responseEsperada =
            new VagaResponse();

    responseEsperada.setId(vagaId);
    responseEsperada.setTitulo(
            "Cozinheiro para evento"
    );

    when(
            vagaRepository.findById(vagaId)
    ).thenReturn(
            Optional.of(vaga)
    );

    when(
            vagaMapper.toResponse(vaga)
    ).thenReturn(responseEsperada);

    Optional<VagaResponse> resultado =
            vagaService.buscarPorId(vagaId);

    assertEquals(
            true,
            resultado.isPresent()
    );

    assertEquals(
            vagaId,
            resultado.get().getId()
    );

    assertEquals(
            "Cozinheiro para evento",
            resultado.get().getTitulo()
    );

    verify(vagaRepository)
            .findById(vagaId);

    verify(vagaMapper)
            .toResponse(vaga);
}

@Test
void deveRetornarOptionalVazioQuandoVagaNaoExistir() {

    Long vagaId = 999L;

    when(
            vagaRepository.findById(vagaId)
    ).thenReturn(Optional.empty());

    Optional<VagaResponse> resultado =
            vagaService.buscarPorId(vagaId);

    assertEquals(
            true,
            resultado.isEmpty()
    );

    verify(vagaRepository)
            .findById(vagaId);

    verify(
            vagaMapper,
            never()
    ).toResponse(any(Vaga.class));
}
@Test
void devePublicarVagaComSucesso() {

    Long vagaId = 10L;

    Empresa empresa = new Empresa();
    empresa.setId(1L);
    empresa.setNomeFantasia("OpenFree");

    Vaga vaga = new Vaga();
    vaga.setId(vagaId);
    vaga.setTitulo("Cozinheiro");
    vaga.setEmpresa(empresa);
    vaga.setStatus(StatusVaga.RASCUNHO);

    VagaResponse response =
            new VagaResponse();

    response.setId(vagaId);
    response.setTitulo("Cozinheiro");

    when(
            empresaAuthService.getEmpresaLogada(authentication)
    ).thenReturn(empresa);

    when(
            vagaRepository.findByIdAndEmpresaId(
                    vagaId,
                    empresa.getId()
            )
    ).thenReturn(Optional.of(vaga));

    when(
            vagaRepository.save(any(Vaga.class))
    ).thenAnswer(invocation -> invocation.getArgument(0));

    when(
            vagaMapper.toResponse(any(Vaga.class))
    ).thenReturn(response);

    VagaResponse resultado =
            vagaService.publicar(
                    vagaId,
                    authentication
            );

    assertNotNull(resultado);

    assertEquals(
            StatusVaga.PUBLICADA,
            vaga.getStatus()
    );

    verify(vagaRepository)
            .save(vaga);

    verify(vagaMapper)
            .toResponse(vaga);
}
@Test
void deveImpedirPublicarVagaJaPublicada() {

    Long vagaId = 10L;

    Empresa empresa = new Empresa();
    empresa.setId(1L);
    empresa.setNomeFantasia("OpenFree");

    Vaga vaga = new Vaga();
    vaga.setId(vagaId);
    vaga.setTitulo("Cozinheiro");
    vaga.setEmpresa(empresa);
    vaga.setStatus(StatusVaga.PUBLICADA);

    when(
            empresaAuthService.getEmpresaLogada(authentication)
    ).thenReturn(empresa);

    when(
            vagaRepository.findByIdAndEmpresaId(
                    vagaId,
                    empresa.getId()
            )
    ).thenReturn(Optional.of(vaga));

    BusinessException exception =
            assertThrows(
                    BusinessException.class,
                    () -> vagaService.publicar(
                            vagaId,
                            authentication
                    )
            );

    assertEquals(
            "Somente vagas em RASCUNHO podem ser publicadas.",
            exception.getMessage()
    );

    verify(
            vagaRepository,
            never()
    ).save(any(Vaga.class));

    verify(
            vagaMapper,
            never()
    ).toResponse(any(Vaga.class));
}


@Test
void deveExcluirVagaComSucesso() {

    Long vagaId = 10L;

    Empresa empresa = new Empresa();
    empresa.setId(1L);
    empresa.setNomeFantasia("OpenFree");

    Vaga vaga = new Vaga();
    vaga.setId(vagaId);
    vaga.setTitulo("Cozinheiro");
    vaga.setEmpresa(empresa);

    when(
            empresaAuthService.getEmpresaLogada(authentication)
    ).thenReturn(empresa);

    when(
            vagaRepository.findByIdAndEmpresaId(
                    vagaId,
                    empresa.getId()
            )
    ).thenReturn(Optional.of(vaga));

    vagaService.excluir(
            vagaId,
            authentication
    );

    verify(vagaRepository)
            .delete(vaga);
}
@Test
void deveImpedirExcluirVagaDeOutraEmpresa() {

    Long vagaId = 10L;

    Empresa empresa = new Empresa();
    empresa.setId(1L);
    empresa.setNomeFantasia("OpenFree");

    when(
            empresaAuthService.getEmpresaLogada(authentication)
    ).thenReturn(empresa);

    when(
            vagaRepository.findByIdAndEmpresaId(
                    vagaId,
                    empresa.getId()
            )
    ).thenReturn(Optional.empty());

    BusinessException exception =
            assertThrows(
                    BusinessException.class,
                    () -> vagaService.excluir(
                            vagaId,
                            authentication
                    )
            );

    assertEquals(
            "Vaga não encontrada ou não pertence à empresa autenticada.",
            exception.getMessage()
    );

    verify(
            vagaRepository,
            never()
    ).delete(any(Vaga.class));
}
@Test
void deveListarVagasDaEmpresa() {

    Long empresaId = 1L;

    Vaga vaga = new Vaga();
    vaga.setId(10L);
    vaga.setTitulo("Cozinheiro");

    VagaResponse response =
            new VagaResponse();

    response.setId(10L);
    response.setTitulo("Cozinheiro");

    when(
            empresaRepository.existsById(empresaId)
    ).thenReturn(true);

    when(
            vagaRepository.findByEmpresaId(
                    empresaId
            )
    ).thenReturn(List.of(vaga));

    when(
            vagaMapper.toResponse(vaga)
    ).thenReturn(response);

    List<VagaResponse> resultado =
            vagaService.listarPorEmpresa(
                    empresaId
            );

    assertEquals(
            1,
            resultado.size()
    );

    assertEquals(
            "Cozinheiro",
            resultado.get(0).getTitulo()
    );

    verify(empresaRepository)
            .existsById(empresaId);

    verify(vagaRepository)
            .findByEmpresaId(empresaId);

    verify(vagaMapper)
            .toResponse(vaga);
}
@Test
void deveLancarExcecaoQuandoEmpresaNaoExistirAoListarVagas() {

    Long empresaId = 999L;

    when(
            empresaRepository.existsById(empresaId)
    ).thenReturn(false);

    BusinessException exception =
            assertThrows(
                    BusinessException.class,
                    () -> vagaService.listarPorEmpresa(
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
    ).findByEmpresaId(empresaId);

    verify(
            vagaMapper,
            never()
    ).toResponse(any(Vaga.class));
}
@Test
void deveBuscarVagasComPaginacao() {

    JobFilterRequest filtro =
            new JobFilterRequest();

    Pageable pageable =
            PageRequest.of(
                    0,
                    10,
                    Sort.by(
                            Sort.Direction.DESC,
                            "createdAt"
                    )
            );

    Vaga vaga = new Vaga();
    vaga.setId(10L);
    vaga.setTitulo("Cozinheiro");

    VagaResponse response =
            new VagaResponse();

    response.setId(10L);
    response.setTitulo("Cozinheiro");

    PageImpl<Vaga> pagina =
            new PageImpl<>(
                    List.of(vaga),
                    pageable,
                    1
            );

    when(
            vagaRepository.findAll(
                    any(org.springframework.data.jpa.domain.Specification.class),
                    eq(pageable)
            )
    ).thenReturn(pagina);

    when(
            vagaMapper.toResponse(vaga)
    ).thenReturn(response);

    PageResponse<VagaResponse> resultado =
            vagaService.buscar(
                    filtro,
                    pageable
            );

    assertNotNull(resultado);

    assertEquals(
            1,
            resultado.getContent().size()
    );

    assertEquals(
            "Cozinheiro",
            resultado.getContent()
                    .get(0)
                    .getTitulo()
    );

    assertEquals(
            0,
            resultado.getPage()
    );

    assertEquals(
            10,
            resultado.getSize()
    );

    assertEquals(
            1L,
            resultado.getTotalElements()
    );

    verify(vagaRepository)
            .findAll(
                    any(org.springframework.data.jpa.domain.Specification.class),
                    eq(pageable)
            );

    verify(vagaMapper)
            .toResponse(vaga);
}

@Test
void deveLancarExcecaoQuandoPageSizeForMaiorQue100() {

    JobFilterRequest filtro =
            new JobFilterRequest();

    Pageable pageable =
            PageRequest.of(
                    0,
                    101,
                    Sort.by("createdAt")
            );

    BusinessException exception =
            assertThrows(
                    BusinessException.class,
                    () -> vagaService.buscar(
                            filtro,
                            pageable
                    )
            );

    assertEquals(
            "O tamanho máximo permitido é de 100 registros por página.",
            exception.getMessage()
    );

    verify(
            vagaRepository,
            never()
    ).findAll(
            any(org.springframework.data.jpa.domain.Specification.class),
            any(Pageable.class)
    );
}

    @Test
void deveLancarExcecaoQuandoCampoDeOrdenacaoForInvalido() {

    JobFilterRequest filtro =
            new JobFilterRequest();

    Pageable pageable =
            PageRequest.of(
                    0,
                    10,
                    Sort.by("campoInexistente")
            );

    BusinessException exception =
            assertThrows(
                    BusinessException.class,
                    () -> vagaService.buscar(
                            filtro,
                            pageable
                    )
            );

    assertEquals(
            true,
            exception.getMessage().contains(
                    "Campo de ordenação inválido"
            )
    );

    assertEquals(
            true,
            exception.getMessage().contains(
                    "campoInexistente"
            )
    );

    verify(
            vagaRepository,
            never()
    ).findAll(
            any(org.springframework.data.jpa.domain.Specification.class),
            any(Pageable.class)
    );
}


}