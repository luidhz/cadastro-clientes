package com.luiz.cadastroclientes.service;

import com.luiz.cadastroclientes.entities.ItemCompra;
import com.luiz.cadastroclientes.exceptions.DatabaseException;
import com.luiz.cadastroclientes.repository.ItemCompraRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemCompraService {

    private final ItemCompraRepository itemCompraRepository;

    public ItemCompra insert(ItemCompra itemCompra) {
        return itemCompraRepository.save(itemCompra);
    }

    public List<ItemCompra> findAll() {
        return itemCompraRepository.findAll();
    }

    public ItemCompra findById(Long id) {
        return itemCompraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ItemCompra nao encontrado com id: " + id));
    }

    public ItemCompra update(Long id, ItemCompra itemCompra) {
        try {
            ItemCompra entidade = itemCompraRepository.getReferenceById(id);
            updateData(entidade, itemCompra);
            return itemCompraRepository.save(entidade);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("ItemCompra nao encontrado com id: " + id);
        }
    }

    public void delete(Long id) {
        try {
            itemCompraRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    private void updateData(ItemCompra entidade, ItemCompra itemCompra) {
        entidade.setCompra(itemCompra.getCompra());
        entidade.setProduto(itemCompra.getProduto());
        entidade.setQuantidade(itemCompra.getQuantidade());
        entidade.setPrecoUnitario(itemCompra.getPrecoUnitario());
    }
}
