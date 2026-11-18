package com.openfree_api.modules.companies.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.openfree_api.common.cnpj.CnpjLookupService;
import com.openfree_api.modules.auth.service.EmpresaAuthService;
import com.openfree_api.modules.candidaturas.repository.CandidaturaRepository;
import com.openfree_api.modules.companies.dto.EmpresaResponse;
import com.openfree_api.modules.companies.entity.Empresa;
import com.openfree_api.modules.companies.entity.TipoPessoa;
import com.openfree_api.modules.companies.mapper.EmpresaMapper;
import com.openfree_api.modules.companies.mapper.EmpresaUsuarioMapper;
import com.openfree_api.modules.companies.repository.EmpresaRepository;
import com.openfree_api.modules.companies.repository.EmpresaUsuarioRepository;
import com.openfree_api.modules.jobs.repository.VagaRepository;
import com.openfree_api.modules.users.repository.UsuarioRepository;

/*
 * GET /companies/{id}: outra pessoa vendo o contratante.
 */
@ExtendWith(MockitoExtension.class)
class EmpresaServicePerfilPublicoTest {

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

    private EmpresaResponse respostaComContato() {
        EmpresaResponse response = new EmpresaResponse();
        response.setNomeFantasia("Contratante");
        response.setEmail("contato@email.com");
        response.setTelefone("11999999999");
        return response;
    }

    @Test
    void pessoaFisicaNaoMostraEmailNemTelefone() {

        Empresa empresa = new Empresa();
        empresa.setTipoPessoa(TipoPessoa.PF);

        when(empresaRepository.findById(1L)).thenReturn(Optional.of(empresa));
        when(empresaMapper.toResponse(empresa)).thenReturn(respostaComContato());

        EmpresaResponse response = empresaService.buscarPorId(1L).orElseThrow();

        assertEquals("Contratante", response.getNomeFantasia());
        assertNull(response.getEmail());
        assertNull(response.getTelefone());
    }

    @Test
    void empresaContinuaMostrandoContatoComercial() {

        Empresa empresa = new Empresa();
        empresa.setTipoPessoa(TipoPessoa.PJ);

        when(empresaRepository.findById(2L)).thenReturn(Optional.of(empresa));
        when(empresaMapper.toResponse(empresa)).thenReturn(respostaComContato());

        EmpresaResponse response = empresaService.buscarPorId(2L).orElseThrow();

        assertEquals("contato@email.com", response.getEmail());
        assertEquals("11999999999", response.getTelefone());
    }
}
