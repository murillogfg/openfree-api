package com.openfree_api.modules.candidaturas.service;

import com.openfree_api.modules.auth.service.EmpresaAuthService;
import com.openfree_api.modules.auth.service.UsuarioAuthService;
import com.openfree_api.modules.candidaturas.dto.CandidaturaResponse;
import com.openfree_api.modules.candidaturas.dto.CreateCandidaturaRequest;
import com.openfree_api.modules.candidaturas.entity.Candidatura;
import com.openfree_api.modules.candidaturas.entity.StatusCandidatura;
import com.openfree_api.modules.candidaturas.mapper.CandidaturaMapper;
import com.openfree_api.modules.candidaturas.repository.CandidaturaRepository;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.jobs.entity.StatusVaga;
import com.openfree_api.modules.jobs.entity.Vaga;
import com.openfree_api.modules.jobs.repository.VagaRepository;
import com.openfree_api.modules.notifications.entity.NotificationType;
import com.openfree_api.modules.notifications.service.NotificationService;
import com.openfree_api.modules.users.entity.Usuario;


import com.openfree_api.common.exception.BusinessException;

import com.openfree_api.modules.candidaturas.dto.CreateCandidaturaRequest;
import com.openfree_api.modules.candidaturas.entity.Candidatura;

import com.openfree_api.modules.companies.entity.Empresa;

import com.openfree_api.modules.jobs.entity.StatusVaga;
import com.openfree_api.modules.jobs.entity.Vaga;

import com.openfree_api.modules.users.entity.Usuario;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;



import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import org.springframework.security.core.Authentication;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.openfree_api.common.exception.BusinessException;

import static org.junit.jupiter.api.Assertions.assertThrows;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import com.openfree_api.modules.chat.service.ChatService;
import com.openfree_api.modules.contracts.service.ContractService;
import com.openfree_api.modules.payments.service.PaymentService;



@ExtendWith(MockitoExtension.class)
class CandidaturaServiceTest {

   @Mock
private CandidaturaRepository candidaturaRepository;

@Mock
private VagaRepository vagaRepository;

@Mock
private UsuarioAuthService usuarioAuthService;

@Mock
private EmpresaAuthService empresaAuthService;

@Mock
private NotificationService notificationService;

@Mock
private ContractService contractService;

@Mock
private ChatService chatService;

@Mock
private PaymentService paymentService;

@Mock
private Authentication authentication;

@Spy
private CandidaturaMapper candidaturaMapper =
        new CandidaturaMapper();

@InjectMocks
private CandidaturaService candidaturaService;

    @Test
    void deveCriarCandidaturaComSucesso() {

        Long vagaId = 1L;

        Usuario usuario = new Usuario();
        usuario.setId(10L);
        usuario.setNome("Murillo");
        usuario.setEmail("murillo@email.com");

        Vaga vaga = new Vaga();
        vaga.setId(vagaId);
        vaga.setTitulo("Cozinheiro para evento");
        vaga.setStatus(StatusVaga.PUBLICADA);

        CreateCandidaturaRequest request =
                new CreateCandidaturaRequest();

        request.setMensagem(
                "Tenho interesse nesta oportunidade."
        );

        request.setValorProposto(
                new BigDecimal("250.00")
        );

        Candidatura candidaturaSalva =
                new Candidatura();

        candidaturaSalva.setUsuario(usuario);
        candidaturaSalva.setVaga(vaga);
        candidaturaSalva.setMensagem(
                request.getMensagem()
        );
        candidaturaSalva.setValorProposto(
                request.getValorProposto()
        );
        candidaturaSalva.setStatus(
                StatusCandidatura.PENDENTE
        );

        when(
                usuarioAuthService.getUsuarioLogado(
                        authentication
                )
        ).thenReturn(usuario);

        when(
                vagaRepository.findById(vagaId)
        ).thenReturn(
                Optional.of(vaga)
        );

        when(
                candidaturaRepository.existsByVagaIdAndUsuarioId(
                        vagaId,
                        usuario.getId()
                )
        ).thenReturn(false);

        when(
                candidaturaRepository.save(
                        org.mockito.ArgumentMatchers.any(
                                Candidatura.class
                        )
                )
        ).thenReturn(candidaturaSalva);

        CandidaturaResponse response =
                candidaturaService.criar(
                        vagaId,
                        request,
                        authentication
                );

        assertNotNull(response);

        assertEquals(
                usuario.getId(),
                response.getUsuarioId()
        );

        assertEquals(
                vaga.getId(),
                response.getVagaId()
        );

        assertEquals(
                "Cozinheiro para evento",
                response.getVagaTitulo()
        );

        assertEquals(
                StatusCandidatura.PENDENTE,
                response.getStatus()
        );

        verify(
                usuarioAuthService
        ).getUsuarioLogado(authentication);

        verify(
                vagaRepository
        ).findById(vagaId);

        verify(
                candidaturaRepository
        ).existsByVagaIdAndUsuarioId(
                vagaId,
                usuario.getId()
        );

        verify(
                candidaturaRepository
        ).save(
                org.mockito.ArgumentMatchers.any(
                        Candidatura.class
                )
        );
    }

    @Test
void deveImpedirCandidaturaDuplicada() {

    Long vagaId = 1L;

    Usuario usuario = new Usuario();
    usuario.setId(10L);
    usuario.setNome("Murillo");
    usuario.setEmail("murillo@email.com");

    Vaga vaga = new Vaga();
    vaga.setId(vagaId);
    vaga.setTitulo("Cozinheiro para evento");
    vaga.setStatus(StatusVaga.PUBLICADA);

    CreateCandidaturaRequest request =
            new CreateCandidaturaRequest();

    request.setMensagem(
            "Tenho interesse nesta oportunidade."
    );

    request.setValorProposto(
            new BigDecimal("250.00")
    );

    when(
            usuarioAuthService.getUsuarioLogado(
                    authentication
            )
    ).thenReturn(usuario);

    when(
            vagaRepository.findById(vagaId)
    ).thenReturn(
            Optional.of(vaga)
    );

    when(
            candidaturaRepository.existsByVagaIdAndUsuarioId(
                    vagaId,
                    usuario.getId()
            )
    ).thenReturn(true);

    BusinessException exception =
            assertThrows(
                    BusinessException.class,
                    () -> candidaturaService.criar(
                            vagaId,
                            request,
                            authentication
                    )
            );

    assertEquals(
            "Você já se candidatou para esta vaga.",
            exception.getMessage()
    );

    verify(
            usuarioAuthService
    ).getUsuarioLogado(authentication);

    verify(
            vagaRepository
    ).findById(vagaId);

    verify(
            candidaturaRepository
    ).existsByVagaIdAndUsuarioId(
            vagaId,
            usuario.getId()
    );

    verify(
            candidaturaRepository,
            never()
    ).save(
            any(Candidatura.class)
    );
}

@Test
void deveImpedirCandidaturaEmVagaNaoPublicada() {

    Long vagaId = 1L;

    Usuario usuario = new Usuario();
    usuario.setId(10L);

    Vaga vaga = new Vaga();
    vaga.setId(vagaId);
    vaga.setStatus(StatusVaga.FINALIZADA);

    CreateCandidaturaRequest request =
            new CreateCandidaturaRequest();

    when(
            usuarioAuthService.getUsuarioLogado(authentication)
    ).thenReturn(usuario);

    when(
            vagaRepository.findById(vagaId)
    ).thenReturn(Optional.of(vaga));

    BusinessException exception =
            assertThrows(
                    BusinessException.class,
                    () -> candidaturaService.criar(
                            vagaId,
                            request,
                            authentication
                    )
            );

    assertEquals(
            "A vaga não está disponível para candidatura.",
            exception.getMessage()
    );

    verify(
            candidaturaRepository,
            never()
    ).save(any(Candidatura.class));
}
@Test
void deveAceitarCandidaturaComSucesso() {

    Long empresaId = 1L;
    Long candidaturaId = 100L;

    Empresa empresa = new Empresa();
    empresa.setId(empresaId);
    empresa.setNomeFantasia("OpenFree");

    Vaga vaga = new Vaga();
    vaga.setId(10L);
    vaga.setStatus(StatusVaga.PUBLICADA);
    vaga.setQuantidadePessoas(3);

    Usuario usuario = new Usuario();
    usuario.setId(20L);
    usuario.setNome("Murillo");
    usuario.setEmail("murillo@email.com");

    Candidatura candidatura = new Candidatura();
    candidatura.setId(candidaturaId);
    candidatura.setUsuario(usuario);
    candidatura.setVaga(vaga);
    candidatura.setStatus(StatusCandidatura.PENDENTE);
    candidatura.setEmpresaVisualizou(false);

    when(
            empresaAuthService.getEmpresaLogada(authentication)
    ).thenReturn(empresa);

    when(
            candidaturaRepository.findByIdAndVagaEmpresaId(
                    candidaturaId,
                    empresaId
            )
    ).thenReturn(Optional.of(candidatura));

    when(
            candidaturaRepository.countByVagaIdAndStatus(
                    vaga.getId(),
                    StatusCandidatura.ACEITA
            )
    ).thenReturn(0L);

    when(
            candidaturaRepository.save(any(Candidatura.class))
    ).thenAnswer(invocation -> invocation.getArgument(0));

    CandidaturaResponse response =
            candidaturaService.aceitar(
                    candidaturaId,
                    authentication
            );

    assertEquals(
            StatusCandidatura.ACEITA,
            response.getStatus()
    );

    assertEquals(
            true,
            response.getEmpresaVisualizou()
    );

    verify(candidaturaRepository)
            .save(any(Candidatura.class));
}

@Test
void deveFinalizarVagaQuandoLimiteForAtingido() {

    Long empresaId = 1L;

    Empresa empresa = new Empresa();
    empresa.setId(empresaId);
    empresa.setNomeFantasia("OpenFree");

    Usuario usuario = new Usuario();
    usuario.setId(20L);
    usuario.setNome("Murillo");
    usuario.setEmail("murillo@email.com");

    Vaga vaga = new Vaga();
    vaga.setId(10L);
    vaga.setTitulo("Cozinheiro");
    vaga.setStatus(StatusVaga.PUBLICADA);

    // Apenas 1 profissional
    vaga.setQuantidadePessoas(1);

    Candidatura candidatura = new Candidatura();
    candidatura.setId(100L);
    candidatura.setUsuario(usuario);
    candidatura.setVaga(vaga);
    candidatura.setStatus(StatusCandidatura.PENDENTE);

    when(
            empresaAuthService.getEmpresaLogada(authentication)
    ).thenReturn(empresa);

    when(
            candidaturaRepository.findByIdAndVagaEmpresaId(
                    100L,
                    empresaId
            )
    ).thenReturn(Optional.of(candidatura));

    // Ainda não existe nenhum aceito
    when(
            candidaturaRepository.countByVagaIdAndStatus(
                    vaga.getId(),
                    StatusCandidatura.ACEITA
            )
    ).thenReturn(0L);

    when(
            candidaturaRepository.save(any(Candidatura.class))
    ).thenAnswer(i -> i.getArgument(0));

    when(
            vagaRepository.save(any(Vaga.class))
    ).thenAnswer(i -> i.getArgument(0));

    when(
            candidaturaRepository.findByVagaIdAndStatusIn(
                    any(),
                    any()
            )
    ).thenReturn(List.of());

    candidaturaService.aceitar(
            100L,
            authentication
    );

    assertEquals(
            StatusVaga.FINALIZADA,
            vaga.getStatus()
    );

    verify(vagaRepository)
            .save(vaga);
}

@Test
void deveRecusarCandidaturasRestantesQuandoVagaForPreenchida() {

    Long empresaId = 1L;
    Long candidaturaAceitaId = 100L;

    Empresa empresa = new Empresa();
    empresa.setId(empresaId);
    empresa.setNomeFantasia("OpenFree");

    Usuario usuarioAceito = new Usuario();
    usuarioAceito.setId(20L);
    usuarioAceito.setNome("Murillo");
    usuarioAceito.setEmail("murillo@email.com");

    Usuario usuarioRestante = new Usuario();
    usuarioRestante.setId(21L);
    usuarioRestante.setNome("Carlos");
    usuarioRestante.setEmail("carlos@email.com");

    Vaga vaga = new Vaga();
    vaga.setId(10L);
    vaga.setTitulo("Cozinheiro");
    vaga.setStatus(StatusVaga.PUBLICADA);
    vaga.setQuantidadePessoas(1);

    Candidatura candidaturaAceita = new Candidatura();
    candidaturaAceita.setId(candidaturaAceitaId);
    candidaturaAceita.setUsuario(usuarioAceito);
    candidaturaAceita.setVaga(vaga);
    candidaturaAceita.setStatus(StatusCandidatura.PENDENTE);
    candidaturaAceita.setEmpresaVisualizou(false);

    Candidatura candidaturaRestante = new Candidatura();
    candidaturaRestante.setId(101L);
    candidaturaRestante.setUsuario(usuarioRestante);
    candidaturaRestante.setVaga(vaga);
    candidaturaRestante.setStatus(StatusCandidatura.PENDENTE);
    candidaturaRestante.setEmpresaVisualizou(false);

    when(
            empresaAuthService.getEmpresaLogada(authentication)
    ).thenReturn(empresa);

    when(
            candidaturaRepository.findByIdAndVagaEmpresaId(
                    candidaturaAceitaId,
                    empresaId
            )
    ).thenReturn(Optional.of(candidaturaAceita));

    when(
            candidaturaRepository.countByVagaIdAndStatus(
                    vaga.getId(),
                    StatusCandidatura.ACEITA
            )
    ).thenReturn(0L);

    when(
            candidaturaRepository.save(any(Candidatura.class))
    ).thenAnswer(invocation -> invocation.getArgument(0));

    when(
            vagaRepository.save(any(Vaga.class))
    ).thenAnswer(invocation -> invocation.getArgument(0));

    when(
            candidaturaRepository.findByVagaIdAndStatusIn(
                    eq(vaga.getId()),
                    any()
            )
    ).thenReturn(List.of(candidaturaRestante));

    candidaturaService.aceitar(
            candidaturaAceitaId,
            authentication
    );

    assertEquals(
            StatusCandidatura.RECUSADA,
            candidaturaRestante.getStatus()
    );

    assertEquals(
            true,
            candidaturaRestante.getEmpresaVisualizou()
    );

    verify(candidaturaRepository)
            .saveAll(List.of(candidaturaRestante));

    verify(notificationService)
            .criarNotificacao(
                    usuarioRestante,
                    "Processo seletivo encerrado",
                    "A vaga \"Cozinheiro\" foi preenchida e sua candidatura não foi selecionada.",
                    NotificationType.INFO
            );
}
    @Test
void deveGerarNotificacaoAoAceitarCandidatura() {

    Long empresaId = 1L;
    Long candidaturaId = 100L;

    Empresa empresa = new Empresa();
    empresa.setId(empresaId);
    empresa.setNomeFantasia("OpenFree");

    Usuario usuario = new Usuario();
    usuario.setId(20L);
    usuario.setNome("Murillo");
    usuario.setEmail("murillo@email.com");

    Vaga vaga = new Vaga();
    vaga.setId(10L);
    vaga.setTitulo("Cozinheiro");
    vaga.setStatus(StatusVaga.PUBLICADA);
    vaga.setQuantidadePessoas(2);

    Candidatura candidatura = new Candidatura();
    candidatura.setId(candidaturaId);
    candidatura.setUsuario(usuario);
    candidatura.setVaga(vaga);
    candidatura.setStatus(StatusCandidatura.PENDENTE);

    when(
            empresaAuthService.getEmpresaLogada(authentication)
    ).thenReturn(empresa);

    when(
            candidaturaRepository.findByIdAndVagaEmpresaId(
                    candidaturaId,
                    empresaId
            )
    ).thenReturn(Optional.of(candidatura));

    when(
            candidaturaRepository.countByVagaIdAndStatus(
                    vaga.getId(),
                    StatusCandidatura.ACEITA
            )
    ).thenReturn(0L);

    when(
            candidaturaRepository.save(any(Candidatura.class))
    ).thenAnswer(invocation -> invocation.getArgument(0));

    candidaturaService.aceitar(
            candidaturaId,
            authentication
    );

    verify(notificationService)
            .criarNotificacao(
                    eq(usuario),
                    eq("Candidatura aceita"),
                    eq("Sua candidatura para a vaga \"Cozinheiro\" foi aceita pela empresa OpenFree."),
                    eq(NotificationType.SUCCESS)
            );
}


@Test
void naoDevePermitirCandidaturaNaPropriaVaga() {

    Long vagaId = 1L;

    Usuario usuario = new Usuario();
    usuario.setId(10L);
    usuario.setNome("Murillo");
    usuario.setEmail("empresa@openfree.com");

    Empresa empresa = new Empresa();
    empresa.setId(20L);
    empresa.setNomeFantasia("OpenFree Empresa");
    empresa.setUsuario(usuario);

    Vaga vaga = new Vaga();
    vaga.setId(vagaId);
    vaga.setTitulo("Desenvolvedor Java");
    vaga.setStatus(StatusVaga.PUBLICADA);
    vaga.setEmpresa(empresa);

    CreateCandidaturaRequest request =
            new CreateCandidaturaRequest();

    when(
            usuarioAuthService
                    .getUsuarioLogado(authentication)
    ).thenReturn(usuario);

    when(
            vagaRepository.findById(vagaId)
    ).thenReturn(
            Optional.of(vaga)
    );

    BusinessException exception =
            assertThrows(
                    BusinessException.class,
                    () ->
                            candidaturaService.criar(
                                    vagaId,
                                    request,
                                    authentication
                            )
            );

    assertEquals(
            "Você não pode se candidatar a uma vaga publicada pela sua própria empresa.",
            exception.getMessage()
    );

    verify(
            candidaturaRepository,
            never()
    ).save(
            any(Candidatura.class)
    );
}


}