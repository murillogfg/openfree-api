package com.openfree_api.modules.users;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.companies.entity.TipoPessoa;
import com.openfree_api.modules.companies.repository.EmpresaRepository;
import com.openfree_api.modules.users.repository.UsuarioRepository;

/*
 * Cadastro escolhendo o lado já na criação da conta.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CadastroPorPerfilTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private EmpresaRepository empresaRepository;
    @Autowired private UsuarioRepository usuarioRepository;

    @Test
    void freelancerContinuaSendoOPadrao() throws Exception {

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Bruno Lima","email":"bruno.perfil@teste.com",
                                 "senha":"123456","aceitouTermos":true}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.role").value("FREELANCER"));
    }

    @Test
    void contratantePessoaFisicaComCpf() throws Exception {

        String resposta = mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Carla Dias","email":"carla.pf@teste.com",
                                 "senha":"123456","aceitouTermos":true,
                                 "perfil":"CONTRATANTE",
                                 "contratante":{"tipoPessoa":"PF","cpf":"111.444.777-35"}}
                                """))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.data.role").value("EMPRESA"))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // O CPF nunca volta na resposta (LGPD).
        assertFalse(resposta.contains("11144477735"));
        assertFalse(resposta.contains("111.444.777-35"));

        Empresa contratante = empresaRepository
                .findByUsuarioEmail("carla.pf@teste.com")
                .orElseThrow();

        assertEquals(TipoPessoa.PF, contratante.getTipoPessoa());
        assertEquals("11144477735", contratante.getCpf());
        assertEquals("Carla Dias", contratante.getNomeFantasia());
    }

    @Test
    void contratantePfSemCpfNaoCriaNada() throws Exception {

        mockMvc.perform(post("/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nome":"Davi Reis","email":"davi.semcpf@teste.com",
                                 "senha":"123456","aceitouTermos":true,
                                 "perfil":"CONTRATANTE",
                                 "contratante":{"tipoPessoa":"PF"}}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("O CPF é obrigatório para contratante pessoa física."));

        assertFalse(usuarioRepository.existsByEmail("davi.semcpf@teste.com"));
    }
}
