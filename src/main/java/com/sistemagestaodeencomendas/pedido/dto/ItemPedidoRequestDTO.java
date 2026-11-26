package com.sistemagestaodeencomendas.pedido.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record ItemPedidoRequestDTO(
        @NotBlank(message = "A descrição do produto é obrigatória.")
        String descricaoProduto,

        @NotNull(message = "A quantidade é obrigatória.")
        @Positive(message = "A quantidade deve ser maior que zero.")
        Integer quantidade,

        @NotBlank(message = "A personalização é obrigatória.")
        String personalizacao,

        @NotNull(message = "O valor unitário é obrigatório.")
        @DecimalMin(value = "0.00", inclusive = true,
                message = "O valor unitário não pode ser negativo.")
        BigDecimal valorUnitario
) {
}
