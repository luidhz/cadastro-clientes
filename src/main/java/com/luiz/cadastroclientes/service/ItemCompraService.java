package com.luiz.cadastroclientes.service;

import com.luiz.cadastroclientes.entities.Compra;
import com.luiz.cadastroclientes.entities.ItemCompra;
import com.luiz.cadastroclientes.entities.Produto;
import com.luiz.cadastroclientes.entities.pk.ItemCompraPk;
import com.luiz.cadastroclientes.exceptions.DatabaseException;
import com.luiz.cadastroclientes.repository.CompraRepository;
import com.luiz.cadastroclientes.repository.ItemCompraRepository;
import com.luiz.cadastroclientes.repository.ProdutoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class ItemCompraService {

    private final ItemCompraRepository itemCompraRepository;
    private final CompraRepository compraRepository;
    private final ProdutoRepository produtoRepository;

    private ItemCompraPk buildId(Long compraId, Long produtoId) {
        Compra compra = compraRepository.getReferenceById(compraId);
        Produto produto = produtoRepository.getReferenceById(produtoId);

        ItemCompraPk itemCompraPk = new ItemCompraPk();

        itemCompraPk.setCompra(compra);
        itemCompraPk.setProduto(produto);

        return itemCompraPk;
    }

    public ItemCompra insert(ItemCompra itemCompra) {
        return itemCompraRepository.save(itemCompra);
    }

    public List<ItemCompra> findAll() {
        return itemCompraRepository.findAll();
    }

    public ItemCompra findById(Long compraId, Long produtoId) {
        ItemCompraPk id = buildId(compraId, produtoId);
        return itemCompraRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ItemCompra nao encontrado com id: " + id));
    }

    public ItemCompra update(Long compraId, Long produtoId, ItemCompra itemCompra) {
        ItemCompraPk id = buildId(compraId, produtoId);

        try {
            ItemCompra entidade = itemCompraRepository.getReferenceById(id);
            updateData(entidade, itemCompra);
            return itemCompraRepository.save(entidade);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("ItemCompra nao encontrado com id: " + compraId);
        }
    }

    public void delete(Long compraId, Long produtoId) {
        ItemCompraPk id = buildId(compraId, produtoId);

        try {
            itemCompraRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            throw new DatabaseException(e.getMessage());
        }
    }

    private void updateData(ItemCompra entidade, ItemCompra itemCompra) {
        entidade.setQuantidade(itemCompra.getQuantidade());
        entidade.setPrecoUnitario(itemCompra.getPrecoUnitario());
    }
}
