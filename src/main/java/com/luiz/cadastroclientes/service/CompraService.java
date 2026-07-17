package com.luiz.cadastroclientes.service;

import com.luiz.cadastroclientes.entities.Compra;
import com.luiz.cadastroclientes.entities.ItemCompra;
import com.luiz.cadastroclientes.entities.Produto;
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
    private final ProdutoService produtoService;

    public Compra insert(Compra compra) {
        compra.setDataCompra(LocalDateTime.now());

        double valorTotal = 0.0;

        for (ItemCompra item : compra.getItens()) {
            item.setCompra(compra);

            Produto produto = produtoService.findById(item.getProduto().getId());
            item.setProduto(produto);
            item.setPrecoUnitario(produto.getPreco());

            valorTotal += produto.getPreco() * item.getQuantidade();
        }

        compra.setValorTotal(valorTotal);

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

        if (compra.getItens() != null) {
            entidade.getItens().clear();

            double valorTotal = 0.0;

            for (ItemCompra item : compra.getItens()) {
                item.setCompra(entidade);

                Produto produto = produtoService.findById(item.getProduto().getId());
                item.setProduto(produto);
                item.setPrecoUnitario(produto.getPreco());

                valorTotal += produto.getPreco() * item.getQuantidade();

                entidade.getItens().add(item);
            }

            entidade.setValorTotal(valorTotal);
        } else {
            entidade.setValorTotal(compra.getValorTotal());
        }
    }
}
