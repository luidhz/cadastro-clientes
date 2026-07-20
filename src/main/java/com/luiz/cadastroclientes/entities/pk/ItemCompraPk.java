package com.luiz.cadastroclientes.entities.pk;

import com.luiz.cadastroclientes.entities.Compra;
import com.luiz.cadastroclientes.entities.Produto;
import jakarta.persistence.Embeddable;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.Objects;

@Embeddable
@Getter
@Setter
public class ItemCompraPk implements Serializable {
    @ManyToOne
    @JoinColumn(name = "compra_id")
    private Compra compra;

    @ManyToOne
    @JoinColumn(name = "produto_id")
    private Produto produto;

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof ItemCompraPk that)) return false;
        return Objects.equals(getCompra(), that.getCompra()) && Objects.equals(getProduto(), that.getProduto());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getCompra(), getProduto());
    }
}
