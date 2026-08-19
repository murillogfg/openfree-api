package com.openfree_api.modules.companies.controller;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;



import com.openfree_api.modules.auth.jwt.JwtService;

import com.openfree_api.modules.companies.dto.EmpresaResponse;
import com.openfree_api.modules.companies.service.EmpresaService;

import com.openfree_api.modules.jobs.service.VagaService;

import org.springframework.security.core.userdetails.UserDetailsService;

@WebMvcTest(EmpresaController.class)
@AutoConfigureMockMvc(addFilters = false)
public class EmpresaControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private EmpresaService empresaService;

    @MockitoBean
    private VagaService vagaService;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private UserDetailsService userDetailsService;

    

    @Test
    void deveListarEmpresas() throws Exception {

        EmpresaResponse empresa =
                new EmpresaResponse();

        empresa.setId(1L);
        empresa.setNomeFantasia("OpenFree");

        when(
                empresaService.listarTodas()
        ).thenReturn(
                List.of(empresa)
        );

        mockMvc.perform(
                get("/companies")
        )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.success")
                                .value(true)
                )
                .andExpect(
                        jsonPath(
                                "$.data[0].nomeFantasia"
                        )
                                .value("OpenFree")
                );

        verify(
                empresaService
        ).listarTodas();
    }


    @Test
    void deveBuscarEmpresaPorIdComSucesso()
            throws Exception {

        Long empresaId =
                1L;

        EmpresaResponse empresa =
                new EmpresaResponse();

        empresa.setId(
                empresaId
        );

        empresa.setNomeFantasia(
                "OpenFree"
        );

        when(
                empresaService
                        .buscarPorId(
                                empresaId
                        )
        ).thenReturn(
                Optional.of(
                        empresa
                )
        );

        mockMvc.perform(
                get(
                        "/companies/{id}",
                        empresaId
                )
        )
                .andExpect(
                        status().isOk()
                )
                .andExpect(
                        jsonPath("$.success")
                                .value(true)
                )
                .andExpect(
                        jsonPath("$.message")
                                .value(
                                        "Empresa encontrada com sucesso."
                                )
                )
                .andExpect(
                        jsonPath("$.data.id")
                                .value(1)
                )
                .andExpect(
                        jsonPath(
                                "$.data.nomeFantasia"
                        )
                                .value("OpenFree")
                );

        verify(
                empresaService
        ).buscarPorId(
                empresaId
        );
    }


    @Test
    void deveRetornar404QuandoEmpresaNaoExistir()
            throws Exception {

        Long empresaId =
                999L;

        when(
                empresaService
                        .buscarPorId(
                                empresaId
                        )
        ).thenReturn(
                Optional.empty()
        );

        mockMvc.perform(
                get(
                        "/companies/{id}",
                        empresaId
                )
        )
                .andExpect(
                        status().isNotFound()
                );

        verify(
                empresaService
        ).buscarPorId(
                empresaId
        );
    }
}