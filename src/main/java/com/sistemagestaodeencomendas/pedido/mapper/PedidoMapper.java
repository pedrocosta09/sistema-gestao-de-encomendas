package com.sistemagestaodeencomendas.pedido.mapper;

import com.sistemagestaodeencomendas.itempedido.entity.ItemPedido;
import com.sistemagestaodeencomendas.pedido.dto.ItemPedidoRequestDTO;
import com.sistemagestaodeencomendas.pedido.dto.ItemPedidoResponseDTO;
import com.sistemagestaodeencomendas.pedido.dto.PedidoResponseDTO;
import com.sistemagestaodeencomendas.pedido.entity.Pedido;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class PedidoMapper {

    public ItemPedido toEntity(ItemPedidoRequestDTO dto) {
        return new ItemPedido(
                dto.descricaoProduto(),
                dto.quantidade(),
                dto.personalizacao(),
                dto.valorUnitario()
        );
    }

    public PedidoResponseDTO toResponseDTO(Pedido pedido) {
        List<ItemPedidoResponseDTO> itens = pedido.getItens()
                .stream()
                .map(this::toItemResponseDTO)
                .toList();

        return new PedidoResponseDTO(
                pedido.getId(),
                pedido.getCliente().getId(),
                pedido.getCliente().getNome(),
                pedido.getDataCriacao(),
                pedido.getDataEntrega(),
                pedido.getValorTotal(),
                pedido.getStatus(),
                pedido.isArteAprovada(),
                pedido.isConfirmado(),
                pedido.getObservacoes(),
                itens
        );
    }

    private ItemPedidoResponseDTO toItemResponseDTO(ItemPedido item) {
        BigDecimal subtotal = item.getValorUnitario()
                .multiply(BigDecimal.valueOf(item.getQuantidade()));

        return new ItemPedidoResponseDTO(
                item.getId(),
                item.getDescricaoProduto(),
                item.getQuantidade(),
                item.getPersonalizacao(),
                item.getValorUnitario(),
                subtotal
        );
    }
}
