package com.luiz.cadastroclientes.enums;

import lombok.Getter;

@Getter
public enum UsuarioRole {
    ADMIN("admin"),
    USUARIO("usuario");

    private final String role;

    UsuarioRole(String role) {
        this.role = role;
    }
}
