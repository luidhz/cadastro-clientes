package com.luiz.cadastroclientes.repository;

import com.luiz.cadastroclientes.entity.Compra;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompraRepository extends JpaRepository<Compra, Long> {
}