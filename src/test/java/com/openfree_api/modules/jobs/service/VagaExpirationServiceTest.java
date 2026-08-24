package com.openfree_api.modules.jobs.service;

import com.openfree_api.modules.jobs.entity.StatusVaga;
import com.openfree_api.modules.jobs.entity.Vaga;
import com.openfree_api.modules.jobs.repository.VagaRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class VagaExpirationServiceTest {

    private static final ZoneId ZONA_NEGOCIO =
            ZoneId.of(
                    "America/Sao_Paulo"
            );


    @Mock
    private VagaRepository vagaRepository;


    @InjectMocks
    private VagaExpirationService service;


    @Test
    void deveExpirarSomenteVagasPublicadasVencidas() {

        LocalDate hoje =
                LocalDate.now(
                        ZONA_NEGOCIO
                );


        Vaga vencida =
                new Vaga();

        vencida.setId(
                1L
        );

        vencida.setStatus(
                StatusVaga.PUBLICADA
        );

        vencida.setDataServico(
                hoje.minusDays(
                        1
                )
        );


        Vaga futura =
                new Vaga();

        futura.setId(
                2L
        );

        futura.setStatus(
                StatusVaga.PUBLICADA
        );

        futura.setDataServico(
                hoje.plusDays(
                        1
                )
        );


        when(
                vagaRepository.findByStatus(
                        StatusVaga.PUBLICADA
                )
        )
                .thenReturn(
                        List.of(
                                vencida,
                                futura
                        )
                );


        int quantidade =
                service.expirarVagasVencidas();


        assertEquals(
                1,
                quantidade
        );

        assertEquals(
                StatusVaga.EXPIRADA,
                vencida.getStatus()
        );

        assertEquals(
                StatusVaga.PUBLICADA,
                futura.getStatus()
        );


        @SuppressWarnings("unchecked")
        ArgumentCaptor<List<Vaga>> captor =
                ArgumentCaptor.forClass(
                        List.class
                );


        verify(
                vagaRepository
        )
                .saveAll(
                        captor.capture()
                );


        assertEquals(
                1,
                captor.getValue()
                        .size()
        );

        assertEquals(
                1L,
                captor.getValue()
                        .get(0)
                        .getId()
        );
    }


    @Test
    void naoDeveExpirarVagaDoDiaAtual() {

        LocalDate hoje =
                LocalDate.now(
                        ZONA_NEGOCIO
                );


        Vaga vaga =
                new Vaga();

        vaga.setId(
                10L
        );

        vaga.setStatus(
                StatusVaga.PUBLICADA
        );

        vaga.setDataServico(
                hoje
        );


        when(
                vagaRepository.findByStatus(
                        StatusVaga.PUBLICADA
                )
        )
                .thenReturn(
                        List.of(
                                vaga
                        )
                );


        int quantidade =
                service.expirarVagasVencidas();


        assertEquals(
                0,
                quantidade
        );

        assertEquals(
                StatusVaga.PUBLICADA,
                vaga.getStatus()
        );


        verify(
                vagaRepository,
                never()
        )
                .saveAll(
                        org.mockito.ArgumentMatchers.anyList()
                );
    }


    @Test
    void naoDeveSalvarQuandoNaoExistiremVagasPublicadas() {

        when(
                vagaRepository.findByStatus(
                        StatusVaga.PUBLICADA
                )
        )
                .thenReturn(
                        List.of()
                );


        int quantidade =
                service.expirarVagasVencidas();


        assertEquals(
                0,
                quantidade
        );


        verify(
                vagaRepository,
                never()
        )
                .saveAll(
                        org.mockito.ArgumentMatchers.anyList()
                );
    }
}