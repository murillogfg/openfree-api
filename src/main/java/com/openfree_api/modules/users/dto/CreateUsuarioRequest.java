package com.openfree_api.modules.users.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateUsuarioRequest {

    @NotBlank(
            message = "O nome é obrigatório."
    )
    private String nome;


    @NotBlank(
            message = "O e-mail é obrigatório."
    )
    @Email(
            message = "Informe um e-mail válido."
    )
    private String email;


    @NotBlank(
            message = "A senha é obrigatória."
    )
    @Size(
            min = 6,
            message = "A senha deve possuir ao menos 6 caracteres."
    )
    private String senha;


    private String telefone;


    /*
     * O frontend envia true somente depois
     * que o usuário marca o checkbox.
     *
     * O backend também valida o aceite,
     * portanto não dependemos apenas
     * da interface Angular.
     */
    @AssertTrue(
            message = "É necessário aceitar os Termos de Uso e a Política de Privacidade."
    )
    private Boolean aceitouTermos;


    public String getNome() {
        return nome;
    }

    public void setNome(
            String nome
    ) {
        this.nome = nome;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(
            String email
    ) {
        this.email = email;
    }


    public String getSenha() {
        return senha;
    }

    public void setSenha(
            String senha
    ) {
        this.senha = senha;
    }


    public String getTelefone() {
        return telefone;
    }

    public void setTelefone(
            String telefone
    ) {
        this.telefone = telefone;
    }


    public Boolean getAceitouTermos() {
        return aceitouTermos;
    }

    public void setAceitouTermos(
            Boolean aceitouTermos
    ) {
        this.aceitouTermos =
                aceitouTermos;
    }
}