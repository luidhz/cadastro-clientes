package com.luiz.cadastroclientes;

import com.luiz.cadastroclientes.entities.Compra;
import com.luiz.cadastroclientes.entities.ItemCompra;
import com.luiz.cadastroclientes.entities.Produto;
import com.luiz.cadastroclientes.exceptions.DatabaseException;
import com.luiz.cadastroclientes.repository.CompraRepository;
import com.luiz.cadastroclientes.repository.ItemCompraRepository;
import com.luiz.cadastroclientes.repository.ProdutoRepository;
import com.luiz.cadastroclientes.service.ItemCompraService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ItemCompraServiceTest {

    @Mock
    private ItemCompraRepository itemCompraRepository;

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ItemCompraService itemCompraService;

    private Compra compra;
    private Produto produto;

    @BeforeEach
    void setUp() {
        compra = new Compra();
        compra.setId(10L);

        produto = new Produto();
        produto.setId(20L);
    }

    @Test
    @DisplayName("insert deve salvar o item de compra")
    void insertDeveSalvarItemDeCompra() {
        ItemCompra item = new ItemCompra();
        when(itemCompraRepository.save(item)).thenReturn(item);

        ItemCompra salvo = itemCompraService.insert(item);

        assertEquals(item, salvo);
        verify(itemCompraRepository, times(1)).save(item);
    }

    @Test
    @DisplayName("findAll deve retornar a lista de itens de compra")
    void findAllDeveRetornarListaDeItens() {
        ItemCompra item = new ItemCompra();
        when(itemCompraRepository.findAll()).thenReturn(List.of(item));

        List<ItemCompra> resultado = itemCompraService.findAll();

        assertThat(resultado).containsExactly(item);
    }

    @Test
    @DisplayName("findById deve retornar o item quando existir")
    void findByIdDeveRetornarItemQuandoExistir() {
        ItemCompra item = new ItemCompra();
        when(compraRepository.getReferenceById(10L)).thenReturn(compra);
        when(produtoRepository.getReferenceById(20L)).thenReturn(produto);
        when(itemCompraRepository.findById(any())).thenReturn(Optional.of(item));

        ItemCompra encontrado = itemCompraService.findById(10L, 20L);

        assertEquals(item, encontrado);
    }

    @Test
    @DisplayName("findById deve lancar EntityNotFoundException quando nao existir")
    void findByIdDeveLancarExcecaoQuandoNaoExistir() {
        when(compraRepository.getReferenceById(10L)).thenReturn(compra);
        when(produtoRepository.getReferenceById(20L)).thenReturn(produto);
        when(itemCompraRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> itemCompraService.findById(10L, 20L));
    }

    @Test
    @DisplayName("update deve atualizar a quantidade e o preco unitario")
    void updateDeveAtualizarQuantidadeEPrecoUnitario() {
        ItemCompra entidade = new ItemCompra();
        entidade.setQuantidade(1);
        entidade.setPrecoUnitario(5.0);

        ItemCompra dadosNovos = new ItemCompra();
        dadosNovos.setQuantidade(3);
        dadosNovos.setPrecoUnitario(15.0);

        when(compraRepository.getReferenceById(10L)).thenReturn(compra);
        when(produtoRepository.getReferenceById(20L)).thenReturn(produto);
        when(itemCompraRepository.getReferenceById(any())).thenReturn(entidade);
        when(itemCompraRepository.save(entidade)).thenReturn(entidade);

        ItemCompra resultado = itemCompraService.update(10L, 20L, dadosNovos);

        assertEquals(3, resultado.getQuantidade());
        assertEquals(15.0, resultado.getPrecoUnitario());
        verify(itemCompraRepository, times(1)).save(entidade);
    }

    @Test
    @DisplayName("update deve lancar EntityNotFoundException quando o item nao existir")
    void updateDeveLancarExcecaoQuandoItemNaoExistir() {
        when(compraRepository.getReferenceById(10L)).thenReturn(compra);
        when(produtoRepository.getReferenceById(20L)).thenReturn(produto);
        when(itemCompraRepository.getReferenceById(any()))
                .thenThrow(new EntityNotFoundException("nao encontrado"));

        ItemCompra dadosNovos = new ItemCompra();
        assertThrows(EntityNotFoundException.class,
                () -> itemCompraService.update(10L, 20L, dadosNovos));

        verify(itemCompraRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete deve remover o item quando existir")
    void deleteDeveRemoverItem() {
        when(compraRepository.getReferenceById(10L)).thenReturn(compra);
        when(produtoRepository.getReferenceById(20L)).thenReturn(produto);

        itemCompraService.delete(10L, 20L);

        verify(itemCompraRepository, times(1)).deleteById(any());
    }

    @Test
    @DisplayName("delete deve lancar DatabaseException quando houver violacao de integridade")
    void deleteDeveLancarDatabaseExceptionQuandoHouverViolacaoDeIntegridade() {
        when(compraRepository.getReferenceById(10L)).thenReturn(compra);
        when(produtoRepository.getReferenceById(20L)).thenReturn(produto);
        doThrow(new DataIntegrityViolationException("violacao"))
                .when(itemCompraRepository).deleteById(any());

        assertThrows(DatabaseException.class, () -> itemCompraService.delete(10L, 20L));
    }
}