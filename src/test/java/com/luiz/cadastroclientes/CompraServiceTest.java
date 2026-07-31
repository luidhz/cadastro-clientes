package com.luiz.cadastroclientes;

import com.luiz.cadastroclientes.dto.response.CompraResponseDTO;
import com.luiz.cadastroclientes.entities.Compra;
import com.luiz.cadastroclientes.entities.ItemCompra;
import com.luiz.cadastroclientes.entities.Produto;
import com.luiz.cadastroclientes.entities.Usuario;
import com.luiz.cadastroclientes.enums.UsuarioRole;
import com.luiz.cadastroclientes.exceptions.DatabaseException;
import com.luiz.cadastroclientes.exceptions.EstoqueInsuficienteException;
import com.luiz.cadastroclientes.repository.CompraRepository;
import com.luiz.cadastroclientes.repository.UsuarioRepository;
import com.luiz.cadastroclientes.service.CompraService;
import com.luiz.cadastroclientes.service.NotificacaoService;
import com.luiz.cadastroclientes.service.ProdutoService;
import jakarta.persistence.EntityNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompraServiceTest {

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private ProdutoService produtoService;

    @Mock
    private NotificacaoService notificacaoService;

    @Mock
    private UsuarioRepository usuarioRepository;

    @InjectMocks
    private CompraService compraService;

    private Usuario usuario;
    private Produto produto;

    @BeforeEach
    void setUp() {
        usuario = new Usuario(1L, "Joao", 30, "joao@email.com", "senha", UsuarioRole.USUARIO);
        produto = new Produto(1L, "123456", "Caneta", 10.0, 5, null, null);
    }

    private Compra criarCompraComItem(Integer quantidadeSolicitada) {
        Compra compra = new Compra();
        compra.setUsuario(new Usuario());
        compra.getUsuario().setId(1L);

        ItemCompra item = new ItemCompra();
        item.setProduto(new Produto());
        item.getProduto().setId(1L);
        item.setQuantidade(quantidadeSolicitada);

        List<ItemCompra> itens = new ArrayList<>();
        itens.add(item);
        compra.setItens(itens);

        return compra;
    }

    @Test
    @DisplayName("insert deve dar baixa no estoque, calcular o valor total, salvar e notificar a compra")
    void insertDeveProcessarCompraComSucesso() {
        Compra compra = criarCompraComItem(2);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(produtoService.findById(1L)).thenReturn(produto);
        when(compraRepository.save(any(Compra.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Compra salva = compraService.insert(compra);

        assertNotNull(salva.getDataCompra());
        assertEquals(usuario, salva.getUsuario());
        assertEquals(3, produto.getQtdeEmEstoque());

        ArgumentCaptor<Produto> produtoCaptor = ArgumentCaptor.forClass(Produto.class);
        verify(produtoService).update(eq(1L), produtoCaptor.capture());
        assertEquals(3, produtoCaptor.getValue().getQtdeEmEstoque());

        assertEquals(10.0, salva.getItens().get(0).getPrecoUnitario());
        assertEquals(20.0, salva.getValorTotal());

        verify(compraRepository).save(compra);
        verify(notificacaoService).notificarCompra(any(CompraResponseDTO.class));
    }

    @Test
    @DisplayName("insert deve lancar EstoqueInsuficienteException quando o estoque for menor que a quantidade pedida")
    void insertDeveLancarExcecaoQuandoEstoqueInsuficiente() {
        Compra compra = criarCompraComItem(10);

        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(usuario));
        when(produtoService.findById(1L)).thenReturn(produto);

        assertThrows(EstoqueInsuficienteException.class, () -> compraService.insert(compra));

        verify(produtoService, never()).update(any(), any());
        verify(compraRepository, never()).save(any());
        verify(notificacaoService, never()).notificarCompra(any());
    }

    @Test
    @DisplayName("findAll deve retornar a lista de compras")
    void findAllDeveRetornarListaDeCompras() {
        Compra compra = new Compra();
        when(compraRepository.findAll()).thenReturn(List.of(compra));

        List<Compra> resultado = compraService.findAll();

        assertThat(resultado).containsExactly(compra);
    }

    @Test
    @DisplayName("findAllDTO deve converter todas as compras para DTO")
    void findAllDTODeveConverterTodasAsCompras() {
        Compra compra = new Compra();
        compra.setUsuario(usuario);
        when(compraRepository.findAll()).thenReturn(List.of(compra));

        List<CompraResponseDTO> resultado = compraService.findAllDTO();

        assertEquals(1, resultado.size());
        assertEquals(usuario.getId(), resultado.get(0).usuario().id());
    }

    @Test
    @DisplayName("findByUsuarioDTO deve retornar as compras do usuario convertidas para DTO")
    void findByUsuarioDTODeveRetornarComprasDoUsuario() {
        Compra compra = new Compra();
        compra.setUsuario(usuario);
        when(compraRepository.findByUsuarioId(1L)).thenReturn(List.of(compra));

        List<CompraResponseDTO> resultado = compraService.findByUsuarioDTO(1L);

        assertEquals(1, resultado.size());
        assertEquals(usuario.getId(), resultado.get(0).usuario().id());
    }

    @Test
    @DisplayName("findById deve retornar a compra quando existir")
    void findByIdDeveRetornarCompraQuandoExistir() {
        Compra compra = new Compra();
        when(compraRepository.findById(1L)).thenReturn(Optional.of(compra));

        Compra encontrada = compraService.findById(1L);

        assertEquals(compra, encontrada);
    }

    @Test
    @DisplayName("findById deve lancar EntityNotFoundException quando nao existir")
    void findByIdDeveLancarExcecaoQuandoNaoExistir() {
        when(compraRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(EntityNotFoundException.class, () -> compraService.findById(99L));
    }

    @Test
    @DisplayName("findByIdDTO deve retornar a compra convertida para DTO")
    void findByIdDTODeveRetornarCompraConvertida() {
        Compra compra = new Compra();
        compra.setUsuario(usuario);
        when(compraRepository.findById(1L)).thenReturn(Optional.of(compra));

        CompraResponseDTO resultado = compraService.findByIdDTO(1L);

        assertEquals(usuario.getId(), resultado.usuario().id());
    }

    @Test
    @DisplayName("update deve substituir os itens e recalcular o valor total quando itens forem informados")
    void updateDeveSubstituirItensQuandoInformados() {
        Compra entidade = new Compra();
        entidade.setItens(new ArrayList<>());

        Compra dadosNovos = criarCompraComItem(3);

        when(compraRepository.getReferenceById(1L)).thenReturn(entidade);
        when(produtoService.findById(1L)).thenReturn(produto);
        when(compraRepository.save(entidade)).thenReturn(entidade);

        Compra resultado = compraService.update(1L, dadosNovos);

        assertEquals(1, resultado.getItens().size());
        assertEquals(10.0, resultado.getItens().get(0).getPrecoUnitario());
        assertEquals(dadosNovos.getUsuario(), resultado.getUsuario());
        verify(compraRepository).save(entidade);
    }

    @Test
    @DisplayName("update deve manter os itens existentes quando nenhum item for informado")
    void updateDeveManterItensQuandoNaoInformados() {
        Compra entidade = new Compra();
        ItemCompra itemExistente = new ItemCompra();
        List<ItemCompra> itensExistentes = new ArrayList<>(List.of(itemExistente));
        entidade.setItens(itensExistentes);

        Compra dadosNovos = new Compra();
        dadosNovos.setUsuario(usuario);
        dadosNovos.setItens(null);

        when(compraRepository.getReferenceById(1L)).thenReturn(entidade);
        when(compraRepository.save(entidade)).thenReturn(entidade);

        Compra resultado = compraService.update(1L, dadosNovos);

        assertEquals(1, resultado.getItens().size());
        assertEquals(itemExistente, resultado.getItens().get(0));
        verify(produtoService, never()).findById(any());
    }

    @Test
    @DisplayName("update deve lancar EntityNotFoundException quando a compra nao existir")
    void updateDeveLancarExcecaoQuandoCompraNaoExistir() {
        when(compraRepository.getReferenceById(99L))
                .thenThrow(new EntityNotFoundException("nao encontrada"));

        Compra dadosNovos = new Compra();
        assertThrows(EntityNotFoundException.class, () -> compraService.update(99L, dadosNovos));

        verify(compraRepository, never()).save(any());
    }

    @Test
    @DisplayName("delete deve remover a compra quando o id existir")
    void deleteDeveRemoverCompra() {
        compraService.delete(1L);

        verify(compraRepository).deleteById(1L);
    }

    @Test
    @DisplayName("delete deve lancar DatabaseException quando houver violacao de integridade")
    void deleteDeveLancarDatabaseExceptionQuandoHouverViolacaoDeIntegridade() {
        doThrow(new DataIntegrityViolationException("violacao"))
                .when(compraRepository).deleteById(1L);

        assertThrows(DatabaseException.class, () -> compraService.delete(1L));
    }
}
