package com.openfree_api.modules.auth.config;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDateTime;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;

import com.openfree_api.modules.auth.jwt.JwtService;
import com.openfree_api.modules.users.entity.Usuario;
import com.openfree_api.modules.users.enums.Role;
import com.openfree_api.modules.users.repository.UsuarioRepository;

/*
 * Garante que um usuário comum autenticado
 * não consegue usar as rotas administrativas
 * (listar/excluir usuários e empresas, gerenciar
 * membros ou ver o painel de outra empresa).
 */
@SpringBootTest
@AutoConfigureMockMvc
class SecurityRulesTest {

    private static final String EMAIL =
            "seguranca.teste@openfree.com";

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private UserDetailsService userDetailsService;

    private String bearer;

    @BeforeEach
    void criarUsuarioComum() {

        if (!usuarioRepository.existsByEmail(EMAIL)) {

            Usuario usuario = new Usuario();
            usuario.setNome("Usuário Comum");
            usuario.setEmail(EMAIL);
            usuario.setSenha(passwordEncoder.encode("123456"));
            usuario.setRole(Role.FREELANCER);
            usuario.setTermsAcceptedAt(LocalDateTime.now());
            usuario.setTermsVersion("1.0");

            usuarioRepository.save(usuario);
        }

        bearer = "Bearer " + jwtService.gerarToken(
                userDetailsService.loadUserByUsername(EMAIL)
        );
    }

    @Test
    void naoPodeListarTodosOsUsuarios() throws Exception {
        mockMvc.perform(get("/usuarios")
                        .header("Authorization", bearer))
                .andExpect(status().isForbidden());
    }

    @Test
    void naoPodeExcluirOutroUsuario() throws Exception {
        mockMvc.perform(delete("/usuarios/999")
                        .header("Authorization", bearer))
                .andExpect(status().isForbidden());
    }

    @Test
    void naoPodeListarTodasAsEmpresas() throws Exception {
        mockMvc.perform(get("/companies")
                        .header("Authorization", bearer))
                .andExpect(status().isForbidden());
    }

    @Test
    void naoPodeExcluirEmpresa() throws Exception {
        mockMvc.perform(delete("/companies/1")
                        .header("Authorization", bearer))
                .andExpect(status().isForbidden());
    }

    @Test
    void naoPodeSeAdicionarComoMembroDeEmpresa() throws Exception {
        mockMvc.perform(post("/companies/1/members")
                        .header("Authorization", bearer)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"usuarioId\":1,\"cargo\":\"OWNER\"}"))
                .andExpect(status().isForbidden());
    }

    @Test
    void naoPodeVerPainelDeEmpresaPeloId() throws Exception {
        mockMvc.perform(get("/companies/1/dashboard")
                        .header("Authorization", bearer))
                .andExpect(status().isForbidden());
    }

    @Test
    void continuaAcessandoOProprioPerfil() throws Exception {
        mockMvc.perform(get("/usuarios/me")
                        .header("Authorization", bearer))
                .andExpect(status().isOk());
    }

    @Test
    void healthCheckEhPublico() throws Exception {
        mockMvc.perform(get("/"))
                .andExpect(status().isOk());
    }
}
