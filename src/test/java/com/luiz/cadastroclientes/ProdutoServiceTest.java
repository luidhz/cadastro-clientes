package com.luiz.cadastroclientes;

import com.luiz.cadastroclientes.entities.Produto;
import com.luiz.cadastroclientes.exceptions.DatabaseException;
import com.luiz.cadastroclientes.repository.ProdutoRepository;
import com.luiz.cadastroclientes.service.ProdutoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProdutoServiceTest {

    @Mock
    private ProdutoRepository produtoRepository;

    @InjectMocks
    private ProdutoService produtoService;

    private Produto produto;

    @BeforeEach
    void setUp() {
        produto = new Produto(1L, "123456", "Caneta", 2.5, 10, null, null);
    }

    @Test
    @DisplayName("insert deve salvar o produto e preencher as datas quando o codigo de barras nao existir")
    void insertDeveSalvarQuandoCodigoDeBarrasNaoExiste() {
        when(produtoRepository.findByCodigoDeBarras("123456")).thenReturn(Optional.empty());
        when(produtoRepository.save(any(Produto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Produto salvo = produtoService.insert(produto);

        assertNotNull(salvo.getDataCriacao());
        assertNotNull(salvo.getDataUltimaAtualizacao());
        verify(produtoRepository, times(1)).save(produto);
    }

    @Test
    @DisplayName("insert deve lancar DatabaseException quando o codigo de barras ja existir")
    void insertDeveLancarExcecaoQuandoCodigoDeBarrasJaExiste() {
        when(produtoRepository.findByCodigoDeBarras("123456")).thenReturn(Optional.of(produto));

        assertThrows(DatabaseException.class, () -> produtoService.insert(produto));

        verify(produtoRepository, never()).save(any());
    }

    @Test
    @DisplayName("findById deve retornar o produto quando existir")
    void findByIdDeveRetornarProdutoQuandoExistir() {
        when(produtoRepository.findById(1L)).thenReturn(Optional.of(produto));

        Produto encontrado = produtoService.findById(1L);

        assertEquals(produto, encontrado);
    }

    @Test
    @DisplayName("findById deve lancar EntityNotFoundException quando nao existir")
    void findByIdDeveLancarExcecaoQuandoNaoExistir() {
        when(produtoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> produtoService.findById(99L));
    }

    @Test
    @DisplayName("findByCodigoDeBarras deve retornar o produto quando existir")
    void findByCodigoDeBarrasDeveRetornarProdutoQuandoExistir() {
        when(produtoRepository.findByCodigoDeBarras("123456")).thenReturn(Optional.of(produto));

        Produto encontrado = produtoService.findByCodigoDeBarras("123456");

        assertEquals(produto, encontrado);
    }

    @Test
    @DisplayName("findByCodigoDeBarras deve lancar EntityNotFoundException quando nao existir")
    void findByCodigoDeBarrasDeveLancarExcecaoQuandoNaoExistir() {
        when(produtoRepository.findByCodigoDeBarras("000000")).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> produtoService.findByCodigoDeBarras("000000"));
    }

    @Test
    @DisplayName("findAll deve retornar a lista de produtos")
    void findAllDeveRetornarListaDeProdutos() {
        when(produtoRepository.findAll()).thenReturn(List.of(produto));

        List<Produto> resultado = produtoService.findAll();

        assertThat(resultado).containsExactly(produto);
    }

    @Test
    @DisplayName("update deve atualizar os dados e a data de ultima atualizacao")
    void updateDeveAtualizarDados() {
        Produto entidade = new Produto(1L, "111", "Antigo", 1.0, 5,
                LocalDateTime.now().minusDays(1), LocalDateTime.now().minusDays(1));
        Produto dadosNovos = new Produto(null, "222", "Novo", 9.9, 50, null, null);

        when(produtoRepository.getReferenceById(1L)).thenReturn(entidade);
        when(produtoRepository.save(entidade)).thenReturn(entidade);

        Produto resultado = produtoService.update(1L, dadosNovos);

        assertEquals("222", resultado.getCodigoDeBarras());
        assertEquals("Novo", resultado.getNome());
        assertEquals(9.9, resultado.getPreco());
        assertEquals(50, resultado.getQtdeEmEstoque());
        assertNotNull(resultado.getDataUltimaAtualizacao());
        verify(produtoRepository, times(1)).save(entidade);
    }

    @Test
    @DisplayName("update deve lancar EntityNotFoundException quando o produto nao existir")
    void updateDeveLancarExcecaoQuandoProdutoNaoExistir() {
        when(produtoRepository.getReferenceById(99L))
                .thenThrow(new EntityNotFoundException("nao encontrado"));

        assertThrows(EntityNotFoundException.class, () -> produtoService.update(99L, produto));

        verify(produtoRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete deve remover o produto quando o id existir")
    void deleteDeveRemoverProduto() {
        produtoService.delete(1L);

        verify(produtoRepository, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("delete deve lancar DatabaseException quando houver violacao de integridade")
    void deleteDeveLancarDatabaseExceptionQuandoHouverViolacaoDeIntegridade() {
        doThrow(new DataIntegrityViolationException("violacao"))
                .when(produtoRepository).deleteById(1L);

        assertThrows(DatabaseException.class, () -> produtoService.delete(1L));
    }
}