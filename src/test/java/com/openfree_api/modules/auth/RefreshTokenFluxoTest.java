package com.openfree_api.modules.auth;

import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.jayway.jsonpath.JsonPath;

/*
 * Fluxo completo do "lembrar de mim" passando pela API real.
 */
@SpringBootTest
@AutoConfigureMockMvc
class RefreshTokenFluxoTest {

    @Autowired
    private MockMvc mockMvc;

    private String criarContaEEntrar(String email) throws Exception {

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"nome\":\"Teste Refresh\",\"email\":\"" + email
                                + "\",\"senha\":\"123456\",\"aceitouTermos\":true}"))
                .andExpect(status().isCreated());

        String login = mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"" + email + "\",\"senha\":\"123456\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.refreshToken").isNotEmpty())
                .andReturn().getResponse().getContentAsString();

        return JsonPath.read(login, "$.refreshToken");
    }

    private String refresh(String token, int statusEsperado) throws Exception {

        return mockMvc.perform(post("/auth/refresh")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + token + "\"}"))
                .andExpect(status().is(statusEsperado))
                .andReturn().getResponse().getContentAsString();
    }

    @Test
    void renovaSemSenhaETrocaOToken() throws Exception {

        String primeiro = criarContaEEntrar("refresh.ok@teste.com");

        String resposta = refresh(primeiro, 200);
        String segundo = JsonPath.read(resposta, "$.refreshToken");
        String acesso = JsonPath.read(resposta, "$.token");

        assertNotEquals(primeiro, segundo);
        assertNotEquals("", acesso);

        // O segundo continua valendo.
        refresh(segundo, 200);
    }

    @Test
    void tokenReutilizadoEncerraTodasAsSessoes() throws Exception {

        String primeiro = criarContaEEntrar("refresh.roubo@teste.com");
        String segundo = JsonPath.read(refresh(primeiro, 200), "$.refreshToken");

        // Alguém usa de novo o token antigo...
        refresh(primeiro, 401);

        // ...e a sessão "legítima" também cai, por segurança.
        refresh(segundo, 401);
    }

    @Test
    void logoutInvalidaOToken() throws Exception {

        String token = criarContaEEntrar("refresh.logout@teste.com");

        mockMvc.perform(post("/auth/logout")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"refreshToken\":\"" + token + "\"}"))
                .andExpect(status().isNoContent());

        refresh(token, 401);
    }
}
