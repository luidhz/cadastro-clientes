package com.luiz.cadastroclientes.repository;

import com.luiz.cadastroclientes.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {

}