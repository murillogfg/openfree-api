package com.openfree_api.modules.companies.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateEmpresaRequest {

    @NotBlank(message = "A razão social é obrigatória.")
    @Size(max = 150, message = "A razão social deve ter no máximo 150 caracteres.")
    private String razaoSocial;

    @NotBlank(message = "O nome fantasia é obrigatório.")
    @Size(max = 120, message = "O nome fantasia deve ter no máximo 120 caracteres.")
    private String nomeFantasia;

    @NotBlank(message = "O CNPJ é obrigatório.")
    @Pattern(
            regexp = "\\d{14}|\\d{2}\\.\\d{3}\\.\\d{3}/\\d{4}-\\d{2}",
            message = "O CNPJ deve conter 14 dígitos ou estar no formato 00.000.000/0000-00."
    )
    private String cnpj;

    @NotBlank(message = "O e-mail é obrigatório.")
    @Email(message = "Informe um e-mail válido.")
    @Size(max = 150, message = "O e-mail deve ter no máximo 150 caracteres.")
    private String email;

    @Size(max = 20, message = "O telefone deve ter no máximo 20 caracteres.")
    private String telefone;

    @Size(max = 2000, message = "A descrição deve ter no máximo 2000 caracteres.")
    private String descricao;

    @Size(max = 500, message = "A URL da logo deve ter no máximo 500 caracteres.")
    private String logo;

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(String telefone) {
        this.telefone = telefone;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    private Long ownerId;

    public Long getOwnerId() {
    return ownerId;
    }

public void setOwnerId(Long ownerId) {
    this.ownerId = ownerId;
    }

}