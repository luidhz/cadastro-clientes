package com.luiz.cadastroclientes.service;

import com.luiz.cadastroclientes.entity.Compra;
import com.luiz.cadastroclientes.entity.ItemCompra;
import com.luiz.cadastroclientes.exceptions.DatabaseException;
import com.luiz.cadastroclientes.repository.CompraRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class CompraService {

    private final CompraRepository compraRepository;

    public Compra insert(Compra compra) {
        compra.setDataCompra(LocalDateTime.now());

        for (ItemCompra item : compra.getItens()) {
            item.setCompra(compra);
        }

        return compraRepository.save(compra);
    }

    public List<Compra> findAll() {
        return compraRepository.findAll();
    }

    public Compra findById(Long id) {
        return compraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Compra nao encontrada com id: " + id));
    }

    public Compra update(Long id, Compra compra) {
        try {
            Compra entidade = compraRepository.getReferenceById(id);
            updateData(entidade, compra);
            return compraRepository.save(entidade);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Compra nao encontrada com id: " + id);
        }
    }

    public void delete(Long id) {
        try {
            compraRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    private void updateData(Compra entidade, Compra compra) {
        entidade.setCliente(compra.getCliente());
        entidade.setValorTotal(compra.getValorTotal());

        if (compra.getItens() != null) {
            entidade.getItens().clear();

            for (ItemCompra item : compra.getItens()) {
                item.setCompra(entidade);
                entidade.getItens().add(item);
            }
        }
    }
}
