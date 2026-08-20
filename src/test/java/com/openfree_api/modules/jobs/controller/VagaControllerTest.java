package com.openfree_api.modules.jobs.controller;

import com.openfree_api.common.response.PageResponse;
import com.openfree_api.modules.jobs.dto.JobFilterRequest;
import com.openfree_api.modules.jobs.dto.VagaResponse;
import com.openfree_api.modules.jobs.service.VagaService;

import com.openfree_api.modules.auth.jwt.JwtAuthenticationFilter;
import com.openfree_api.modules.auth.security.CustomAuthenticationEntryPoint;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import org.springframework.data.domain.Pageable;

import org.springframework.http.MediaType;

import org.springframework.security.core.Authentication;

import org.springframework.test.context.bean.override.mockito.MockitoBean;

import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.nullable;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(VagaController.class)
@AutoConfigureMockMvc(addFilters = false)
class VagaControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockitoBean
    private VagaService vagaService;


    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @MockitoBean
    private CustomAuthenticationEntryPoint authenticationEntryPoint;


    /*
     * =====================================================
     * LISTAGEM PÚBLICA
     * =====================================================
     */


    @Test
    void deveListarVagasComPaginacao() throws Exception {

        PageResponse<VagaResponse> pagina =
                new PageResponse<>();


        pagina.setContent(
                List.of()
        );

        pagina.setPage(
                0
        );

        pagina.setSize(
                10
        );

        pagina.setTotalElements(
                0
        );

        pagina.setTotalPages(
                0
        );

        pagina.setFirst(
                true
        );

        pagina.setLast(
                true
        );

        pagina.setEmpty(
                true
        );


        /*
         * O controller público agora chama
         * buscarPublicadas(), e não buscar().
         */
        when(
                vagaService.buscarPublicadas(
                        any(JobFilterRequest.class),
                        any(Pageable.class)
                )
        )
                .thenReturn(
                        pagina
                );


        mockMvc.perform(
                        get("/jobs")
                                .param(
                                        "page",
                                        "0"
                                )
                                .param(
                                        "size",
                                        "10"
                                )
                                .param(
                                        "sort",
                                        "createdAt,desc"
                                )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.success"
                        ).value(
                                true
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.message"
                        ).value(
                                "Vagas listadas com sucesso."
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.data.page"
                        ).value(
                                0
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.data.size"
                        ).value(
                                10
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.data.totalElements"
                        ).value(
                                0
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.data.content"
                        ).isArray()
                )
                .andExpect(
                        jsonPath(
                                "$.data.empty"
                        ).value(
                                true
                        )
                );


        verify(
                vagaService
        )
                .buscarPublicadas(
                        any(JobFilterRequest.class),
                        any(Pageable.class)
                );
    }


    /*
     * =====================================================
     * DETALHE PÚBLICO
     * =====================================================
     */


    @Test
    void deveRetornarVagaQuandoIdExistir() throws Exception {

        VagaResponse vaga =
                new VagaResponse();


        vaga.setId(
                1L
        );

        vaga.setTitulo(
                "Cozinheiro para evento"
        );


        /*
         * O detalhe público agora consulta
         * apenas vagas PUBLICADA.
         */
        when(
                vagaService.buscarPublicadaPorId(
                        1L
                )
        )
                .thenReturn(
                        Optional.of(
                                vaga
                        )
                );


        mockMvc.perform(
                        get(
                                "/jobs/{id}",
                                1L
                        )
                )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath(
                                "$.success"
                        ).value(
                                true
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.message"
                        ).value(
                                "Vaga encontrada com sucesso."
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.data.id"
                        ).value(
                                1
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.data.titulo"
                        ).value(
                                "Cozinheiro para evento"
                        )
                );


        verify(
                vagaService
        )
                .buscarPublicadaPorId(
                        1L
                );
    }


    @Test
    void deveRetornar404QuandoVagaNaoExistir() throws Exception {

        when(
                vagaService.buscarPublicadaPorId(
                        999L
                )
        )
                .thenReturn(
                        Optional.empty()
                );


        mockMvc.perform(
                        get(
                                "/jobs/{id}",
                                999L
                        )
                )
                .andExpect(
                        status().isNotFound()
                );


        verify(
                vagaService
        )
                .buscarPublicadaPorId(
                        999L
                );
    }


    /*
     * =====================================================
     * CRIAÇÃO
     * =====================================================
     */


    @Test
    void deveCriarVagaEDevolver201() throws Exception {

        VagaResponse vagaCriada =
                new VagaResponse();


        vagaCriada.setId(
                10L
        );

        vagaCriada.setTitulo(
                "Garçom para evento"
        );


        when(
                vagaService.criar(
                        any(),
                        nullable(
                                Authentication.class
                        )
                )
        )
                .thenReturn(
                        vagaCriada
                );


        String json =
                """
                {
                  "empresaId": 1,
                  "titulo": "Garçom para evento",
                  "descricao": "Atendimento durante um evento.",
                  "requisitos": "Experiência com atendimento.",
                  "cidade": "São Paulo",
                  "estado": "SP",
                  "valor": 250.00,
                  "quantidadePessoas": 2,
                  "dataServico": "2099-08-20",
                  "horarioInicio": "18:00:00",
                  "horarioFim": "23:00:00"
                }
                """;


        mockMvc.perform(
                        post(
                                "/jobs"
                        )
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content(
                                        json
                                )
                )
                .andExpect(
                        status().isCreated()
                )
                .andExpect(
                        jsonPath(
                                "$.success"
                        ).value(
                                true
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.message"
                        ).value(
                                "Vaga criada com sucesso."
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.data.id"
                        ).value(
                                10
                        )
                )
                .andExpect(
                        jsonPath(
                                "$.data.titulo"
                        ).value(
                                "Garçom para evento"
                        )
                );


        verify(
                vagaService
        )
                .criar(
                        any(),
                        nullable(
                                Authentication.class
                        )
                );
    }
}