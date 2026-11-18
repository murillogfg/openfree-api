package com.openfree_api.modules.companies.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openfree_api.common.cnpj.CnpjLookupService;
import com.openfree_api.common.exception.BusinessException;
import com.openfree_api.modules.auth.service.EmpresaAuthService;
import com.openfree_api.modules.candidaturas.repository.CandidaturaRepository;
import com.openfree_api.modules.companies.dto.CreateEmpresaRequest;
import com.openfree_api.modules.companies.dto.EmpresaResponse;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.companies.entity.TipoPessoa;
import com.openfree_api.modules.companies.mapper.EmpresaMapper;
import com.openfree_api.modules.companies.mapper.EmpresaUsuarioMapper;
import com.openfree_api.modules.companies.repository.EmpresaRepository;
import com.openfree_api.modules.companies.repository.EmpresaUsuarioRepository;
import com.openfree_api.modules.jobs.repository.VagaRepository;
import com.openfree_api.modules.users.entity.Usuario;
import com.openfree_api.modules.users.enums.Role;
import com.openfree_api.modules.users.repository.UsuarioRepository;

/*
 * Contratante pessoa física (CPF), criado no cadastro.
 */
@ExtendWith(MockitoExtension.class)
class EmpresaServicePessoaFisicaTest {

    @Mock private EmpresaRepository empresaRepository;
    @Mock private EmpresaMapper empresaMapper;
    @Mock private EmpresaUsuarioRepository empresaUsuarioRepository;
    @Mock private UsuarioRepository usuarioRepository;
    @Mock private EmpresaUsuarioMapper empresaUsuarioMapper;
    @Mock private VagaRepository vagaRepository;
    @Mock private CandidaturaRepository candidaturaRepository;
    @Mock private EmpresaAuthService empresaAuthService;
    @Mock private CnpjLookupService cnpjLookupService;

    @InjectMocks
    private EmpresaService empresaService;

    private Usuario dono() {
        Usuario usuario = new Usuario();
        usuario.setId(5L);
        usuario.setNome("Ana Souza");
        usuario.setEmail("ana@email.com");
        usuario.setRole(Role.FREELANCER);
        return usuario;
    }

    private CreateEmpresaRequest requestPf(String cpf) {
        CreateEmpresaRequest request = new CreateEmpresaRequest();
        request.setTipoPessoa(TipoPessoa.PF);
        request.setCpf(cpf);
        request.setEmail("ana@email.com");
        request.setNomeFantasia("Ana Souza");
        request.setRazaoSocial("Ana Souza");
        return request;
    }

    @Test
    void criaContratantePessoaFisicaSemConsultarCnpj() {

        Usuario dono = dono();
        CreateEmpresaRequest request = requestPf("529.982.247-25");
        Empresa mapeada = new Empresa();

        when(empresaRepository.existsByCpf("52998224725")).thenReturn(false);
        when(empresaRepository.existsByEmail("ana@email.com")).thenReturn(false);
        when(empresaRepository.findByUsuarioEmail("ana@email.com")).thenReturn(Optional.empty());
        when(empresaMapper.toEntity(request)).thenReturn(mapeada);
        when(empresaRepository.save(mapeada)).thenReturn(mapeada);
        when(empresaMapper.toResponse(mapeada)).thenReturn(new EmpresaResponse());

        empresaService.criarParaUsuario(dono, request);

        assertEquals(TipoPessoa.PF, mapeada.getTipoPessoa());
        assertEquals("52998224725", mapeada.getCpf());
        assertNull(mapeada.getCnpj());
        assertEquals(false, mapeada.getCnpjVerificado());
        assertEquals(Role.EMPRESA, dono.getRole());
        verify(cnpjLookupService, never()).consultar(any());
    }

    @Test
    void recusaCpfInvalido() {

        BusinessException erro = assertThrows(
                BusinessException.class,
                () -> empresaService.criarParaUsuario(dono(), requestPf("529.982.247-24"))
        );

        assertEquals("Informe um CPF válido.", erro.getMessage());
        verify(empresaRepository, never()).save(any());
    }

    @Test
    void recusaCpfJaCadastrado() {

        when(empresaRepository.existsByCpf("52998224725")).thenReturn(true);

        BusinessException erro = assertThrows(
                BusinessException.class,
                () -> empresaService.criarParaUsuario(dono(), requestPf("52998224725"))
        );

        assertEquals("Já existe um contratante cadastrado com este CPF.", erro.getMessage());
        verify(empresaRepository, never()).save(any());
    }

    @Test
    void empresaAntigaSemTipoContaComoPj() {
        assertEquals(TipoPessoa.PJ, new Empresa().getTipoPessoa());
    }
}
