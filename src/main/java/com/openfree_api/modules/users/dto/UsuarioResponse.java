package com.openfree_api.modules.users.dto;

import com.openfree_api.modules.users.enums.Role;
public class UsuarioResponse {

    private Long id;
    private String nome;
    private String email;
    private String telefone;
    private Role role;

public Role getRole() {
    return role;
}

public void setRole(Role role) {
    this.role = role;
}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
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

    
    }
