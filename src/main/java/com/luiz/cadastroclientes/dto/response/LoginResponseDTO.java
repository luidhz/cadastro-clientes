package com.luiz.cadastroclientes.dto.response;

public record LoginResponseDTO(String token, String tipo, String nome, String email, String role) {
    public LoginResponseDTO(String token, String nome, String email, String role) {
        this(token, "Bearer", nome, email, role);
    }
}
