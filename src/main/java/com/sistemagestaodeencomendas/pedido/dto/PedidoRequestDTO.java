package com.sistemagestaodeencomendas.pedido.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public record PedidoRequestDTO(
        @NotNull(message = "O identificador do cliente é obrigatório.")
        Long clienteId,

        @NotNull(message = "A data de entrega é obrigatória.")
        LocalDate dataEntrega,

        @NotEmpty(message = "O pedido deve possuir pelo menos um item.")
        List<@Valid ItemPedidoRequestDTO> itens
) {
}
