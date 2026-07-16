package com.luiz.cadastroclientes.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "clientes")
@NoArgsConstructor
@Getter
@Setter
public class Cliente {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;
    private Integer idade;

    @Column(unique = true)
    private String email;

    private String senha;

    @OneToMany(mappedBy = "cliente")
    @Getter(onMethod_ = @JsonIgnore)
    private List<Compra> compras = new ArrayList<>();

    public Cliente(Long id, String nome, Integer idade, String email, String senha) {
        this.id = id;
        this.nome = nome;
        this.idade = idade;
        this.email = email;
        this.senha = senha;
    }
}
