
package com.sistemagestaodeencomendas.pagamento.entity;

import com.sistemagestaodeencomendas.pagamento.enums.FormaPagamento;
import com.sistemagestaodeencomendas.pedido.entity.Pedido;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pagamentos")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Pagamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valor;

    private LocalDateTime dataPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FormaPagamento formaPagamento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @Column(nullable = false)
    private boolean estornado = false;

    public Pagamento(
            BigDecimal valor,
            FormaPagamento formaPagamento) {

        validarValor(valor);

        if (formaPagamento == null) {
            throw new IllegalArgumentException(
                    "A forma de pagamento é obrigatória."
            );
        }

        this.valor = valor;
        this.formaPagamento = formaPagamento;
    }

    public void vincularPedido(Pedido pedido) {
        if (pedido == null) {
            throw new IllegalArgumentException(
                    "O pedido é obrigatório."
            );
        }

        if (this.pedido != null && this.pedido != pedido) {
            throw new IllegalStateException(
                    "O pagamento já pertence a outro pedido."
            );
        }

        this.pedido = pedido;
    }

    public void registrarPagamento() {
        if (pedido == null) {
            throw new IllegalStateException(
                    "O pagamento precisa estar associado a um pedido."
            );
        }

        if (dataPagamento != null) {
            throw new IllegalStateException(
                    "Este pagamento já foi registrado."
            );
        }

        if (estornado) {
            throw new IllegalStateException(
                    "Um pagamento estornado não pode ser registrado novamente."
            );
        }

        pedido.validarNovoPagamento(valor);

        this.dataPagamento = LocalDateTime.now();
    }

    public void estornar() {
        if (dataPagamento == null) {
            throw new IllegalStateException(
                    "Não é possível estornar um pagamento não registrado."
            );
        }

        if (estornado) {
            throw new IllegalStateException(
                    "Este pagamento já foi estornado."
            );
        }

        throw new UnsupportedOperationException(
                "A política de estorno ainda não foi definida."
        );
    }

    private void validarValor(BigDecimal valor) {
        if (valor == null ||
                valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "O valor do pagamento deve ser maior que zero."
            );
        }
    }
}
