
package com.sistemagestaodeencomendas.consumomaterial.entity;

import com.sistemagestaodeencomendas.material.entity.Material;
import com.sistemagestaodeencomendas.itempedido.entity.ItemPedido;
import com.sistemagestaodeencomendas.pedido.entity.Pedido;
import com.sistemagestaodeencomendas.pedido.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "consumos_materiais")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ConsumoMaterial {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantidadeNecessaria;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "item_pedido_id", nullable = false)
    private ItemPedido itemPedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "material_id", nullable = false)
    private Material material;

    public ConsumoMaterial(
            ItemPedido itemPedido,
            Material material,
            BigDecimal quantidadeNecessaria) {

        if (itemPedido == null) {
            throw new IllegalArgumentException(
                    "O item do pedido é obrigatório."
            );
        }

        if (material == null) {
            throw new IllegalArgumentException(
                    "O material é obrigatório."
            );
        }

        this.itemPedido = itemPedido;
        this.material = material;

        alterarQuantidadeNecessaria(quantidadeNecessaria);
    }

    public void alterarQuantidadeNecessaria(
            BigDecimal novaQuantidade) {

        if (novaQuantidade == null ||
                novaQuantidade.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade necessária deve ser maior que zero."
            );
        }

        if (itemPedido != null && itemPedido.getPedido() != null) {

            Pedido pedido = itemPedido.getPedido();

            if (pedido.isConfirmado()) {
                throw new IllegalStateException(
                        "Não é possível alterar o consumo após a confirmação do pedido."
                );
            }

            if (pedido.getStatus() == StatusPedido.ENTREGUE ||
                    pedido.getStatus() == StatusPedido.CANCELADO) {
                throw new IllegalStateException(
                        "Não é possível alterar o consumo de um pedido encerrado."
                );
            }
        }

        this.quantidadeNecessaria = novaQuantidade;
    }
}
