package com.openfree_api.modules.users.dto;

import com.openfree_api.modules.companies.entity.TipoPessoa;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

/*
 * Dados do contratante enviados junto com o cadastro
 * quando a pessoa escolhe "Quero contratar".
 *
 * PF -> cpf obrigatório; o nome exibido é o nome do usuário.
 * PJ -> cnpj e nomeFantasia obrigatórios.
 *
 * As regras de obrigatoriedade por tipo ficam no service.
 */
public class CadastroContratanteRequest {

    @NotNull(
            message = "Informe se o contratante é pessoa física ou empresa."
    )
    private TipoPessoa tipoPessoa;

    @Pattern(
            regexp = "\\d{11}|\\d{3}\\.\\d{3}\\.\\d{3}-\\d{2}",
            message = "O CPF deve conter 11 dígitos ou estar no formato 000.000.000-00."
    )
    private String cpf;

    @Pattern(
            regexp = "\\d{14}|\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}",
            message = "O CNPJ deve conter 14 dígitos ou estar no formato 00.000.000/0000-00."
    )
    private String cnpj;

    @Size(
            max = 120,
            message = "O nome fantasia deve ter no máximo 120 caracteres."
    )
    private String nomeFantasia;

    @Size(
            max = 150,
            message = "A razão social deve ter no máximo 150 caracteres."
    )
    private String razaoSocial;

    @Size(
            max = 100,
            message = "A cidade deve ter no máximo 100 caracteres."
    )
    private String cidade;

    @Size(
            min = 2,
            max = 2,
            message = "O estado deve conter a sigla com 2 caracteres."
    )
    private String estado;

    public TipoPessoa getTipoPessoa() {
        return tipoPessoa;
    }

    public void setTipoPessoa(TipoPessoa tipoPessoa) {
        this.tipoPessoa = tipoPessoa;
    }

    public String getCpf() {
        return cpf;
    }

    public void setCpf(String cpf) {
        this.cpf = cpf;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
