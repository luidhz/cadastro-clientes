package com.luiz.cadastroclientes.dto.response;

import com.luiz.cadastroclientes.entities.Compra;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public record CompraResponseDTO(
        Long id,
        LocalDateTime dataCompra,
        Double valorTotal,
        UsuarioResumoDTO usuario,
        List<ItemCompraResponseDTO> itens) {

    public CompraResponseDTO(Compra compra) {
        this(
                compra.getId(),
                compra.getDataCompra(),
                compra.getValorTotal(),
                new UsuarioResumoDTO(compra.getUsuario()),
                compra.getItens().stream().map(ItemCompraResponseDTO::new).collect(Collectors.toList())

        );
    }
}
