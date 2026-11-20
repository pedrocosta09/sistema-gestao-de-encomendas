package com.sistemagestaodeencomendas.pedido.dto;

import java.math.BigDecimal;

public record ItemPedidoResponseDTO(
        Long id,
        String descricaoProduto,
        Integer quantidade,
        String personalizacao,
        BigDecimal valorUnitario,
        BigDecimal subtotal
) {
}
