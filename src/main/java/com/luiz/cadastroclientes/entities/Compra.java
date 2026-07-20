package com.luiz.cadastroclientes.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "compra")
@NoArgsConstructor
@Getter
@Setter
public class Compra {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDateTime dataCompra;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    @OneToMany(mappedBy = "id.compra", cascade = CascadeType.ALL)
    private List<ItemCompra> itens = new ArrayList<>();

    @Column(name = "valor_total")
    private Double valorTotal;

    public Compra(Long id, LocalDateTime dataCompra, Cliente cliente, Double valorTotal) {
        this.id = id;
        this.dataCompra = dataCompra;
        this.cliente = cliente;
        this.valorTotal = valorTotal;
    }
}
