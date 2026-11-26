
package com.sistemagestaodeencomendas.itempedido.entity;

import com.sistemagestaodeencomendas.pedido.enums.StatusPedido;
import com.sistemagestaodeencomendas.pedido.entity.Pedido;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "itens_pedido")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class ItemPedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String descricaoProduto;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(nullable = false, length = 2000)
    private String personalizacao;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorUnitario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    public ItemPedido(
            String descricaoProduto,
            Integer quantidade,
            String personalizacao,
            BigDecimal valorUnitario) {

        if (descricaoProduto == null ||
                descricaoProduto.isBlank()) {
            throw new IllegalArgumentException(
                    "A descrição do produto é obrigatória."
            );
        }

        if (personalizacao == null ||
                personalizacao.isBlank()) {
            throw new IllegalArgumentException(
                    "A personalização é obrigatória."
            );
        }

        validarQuantidade(quantidade);
        validarValor(valorUnitario);

        this.descricaoProduto = descricaoProduto.trim();
        this.quantidade = quantidade;
        this.personalizacao = personalizacao.trim();
        this.valorUnitario = valorUnitario;
    }

    public void vincularPedido(Pedido pedido) {        if (pedido == null) {
    public void vincularPedido(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException(
                    "O pedido é obrigatório."
            );
        }

        if (this.pedido != null && this.pedido != pedido) {
            throw new IllegalStateException(
                    "Este item já pertence a outro pedido."
            );
        }

        this.pedido = pedido;
    }

    public void alterarPersonalizacao(
            String novaPersonalizacao) {

        verificarAlteracaoPermitida();

        if (novaPersonalizacao == null ||
                novaPersonalizacao.isBlank()) {
            throw new IllegalArgumentException(
                    "A personalização não pode estar vazia."
            );
        }

        this.personalizacao = novaPersonalizacao.trim();
    }

    public void alterarQuantidade(Integer novaQuantidade) {
        verificarAlteracaoPermitida();
        validarQuantidade(novaQuantidade);

        this.quantidade = novaQuantidade;

        if (pedido != null) {
            pedido.recalcularValorTotal();
        }
    }

    public void atualizarValorUnitario(BigDecimal novoValor) {
        verificarAlteracaoPermitida();
        validarValor(novoValor);

        this.valorUnitario = novoValor;

        if (pedido != null) {
            pedido.recalcularValorTotal();
        }
    }

    private void verificarAlteracaoPermitida() {
        if (pedido == null) {
            return;
        }

        if (pedido.isArteAprovada() || pedido.isConfirmado()) {
            throw new IllegalStateException(
                    "Não é possível alterar o item após a aprovação da arte ou confirmação do pedido."
            );
        }

        StatusPedido status = pedido.getStatus();

        if (status == StatusPedido.ENTREGUE ||
                status == StatusPedido.CANCELADO) {
            throw new IllegalStateException(
                    "Não é possível alterar itens de um pedido encerrado."
            );
        }
    }

    private void validarQuantidade(Integer quantidade) {
        if (quantidade == null || quantidade <= 0) {
            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero."
            );
        }
    }

    private void validarValor(BigDecimal valor) {
        if (valor == null ||
                valor.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException(
                    "O valor unitário não pode ser negativo."
            );
        }
    }
}
