package com.luiz.cadastroclientes.entities;

import com.luiz.cadastroclientes.entities.pk.ItemCompraPk;
import jakarta.persistence.*;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "item_compra")
@NoArgsConstructor
public class ItemCompra {
    @EmbeddedId
    private ItemCompraPk id = new ItemCompraPk();
    private Integer quantidade;

    @Column(name = "preco_unitario")
    private Double precoUnitario;

    public ItemCompra(Compra compra, Produto produto, Integer quantidade, Double precoUnitario) {
        id.setCompra(compra);
        id.setProduto(produto);
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }

    public Compra getCompra() {
        return id.getCompra();
    }

    public void setCompra(Compra compra) {
        id.setCompra(compra);
    }

    public Produto getProduto() {
        return id.getProduto();
    }

    public void setProduto(Produto produto) {
        id.setProduto(produto);
    }

    public Integer getQuantidade() {
        return quantidade;
    }

    public void setQuantidade(Integer quantidade) {
        this.quantidade = quantidade;
    }

    public Double getPrecoUnitario() {
        return precoUnitario;
    }

    public void setPrecoUnitario(Double precoUnitario) {
        this.precoUnitario = precoUnitario;
    }

    public Double getSubTotal() {
        return precoUnitario * quantidade;
    }
}
