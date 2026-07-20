package com.luiz.cadastroclientes.repository;

import com.luiz.cadastroclientes.entities.ItemCompra;
import com.luiz.cadastroclientes.entities.pk.ItemCompraPk;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemCompraRepository extends JpaRepository<ItemCompra, ItemCompraPk> {
}
