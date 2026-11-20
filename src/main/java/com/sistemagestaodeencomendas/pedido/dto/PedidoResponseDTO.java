package com.sistemagestaodeencomendas.pedido.dto;

import com.sistemagestaodeencomendas.pedido.enums.StatusPedido;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoResponseDTO(
        Long id,
        Long clienteId,
        String nomeCliente,
        LocalDateTime dataCriacao,
        LocalDate dataEntrega,
        BigDecimal valorTotal,
        StatusPedido status,
        boolean arteAprovada,
        boolean confirmado,
        String observacoes,
        List<ItemPedidoResponseDTO> itens
) {
}
