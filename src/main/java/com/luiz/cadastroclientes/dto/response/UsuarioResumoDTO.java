package com.luiz.cadastroclientes.dto.response;

import com.luiz.cadastroclientes.entities.Usuario;

public record UsuarioResumoDTO(Long id, String nome, String email) {
    public UsuarioResumoDTO(Usuario usuario) {
        this(usuario.getId(), usuario.getNome(), usuario.getEmail());
    }
}
