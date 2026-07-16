package com.luiz.cadastroclientes.repository;

import com.luiz.cadastroclientes.entities.Produto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    Optional<Produto> findByCodigoDeBarras(String codigoDeBarras);

    Long id(Long id);
}
