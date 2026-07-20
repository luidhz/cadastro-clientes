package com.luiz.cadastroclientes.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.luiz.cadastroclientes.enums.UsuarioRole;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "usuarios")
@NoArgsConstructor
@Getter
@Setter
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private Integer idade;

    @Column(unique = true)
    private String email;

    private String senha;

    @Enumerated(EnumType.STRING)
    private UsuarioRole role;

    @OneToMany(mappedBy = "usuario")
    @Getter(onMethod_ = @JsonIgnore)
    private List<Compra> compras = new ArrayList<>();

    public Usuario(Long id, String nome, Integer idade, String email, String senha, UsuarioRole role) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.email = email;
        this.senha = senha;
        this.role = role;
    }
}
