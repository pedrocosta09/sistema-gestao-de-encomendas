
package com.sistemagestaodeencomendas.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "historico_alteracoes")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class HistoricoAlteracao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String descricao;

    @Column(nullable = false, updatable = false)
    private LocalDateTime dataAlteracao;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private Pedido pedido;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "funcionario_id", nullable = false)
    private Funcionario funcionario;

    public HistoricoAlteracao(
            Pedido pedido,
            Funcionario funcionario,
            String descricao) {

        if (pedido == null) {
            throw new IllegalArgumentException(
                    "O pedido é obrigatório."
            );
        }

        if (funcionario == null) {
            throw new IllegalArgumentException(
                    "O funcionário é obrigatório."
            );
        }

        if (descricao == null || descricao.isBlank()) {
            throw new IllegalArgumentException(
                    "A descrição da alteração é obrigatória."
            );
        }

        this.pedido = pedido;
        this.funcionario = funcionario;
        this.descricao = descricao.trim();
        this.dataAlteracao = LocalDateTime.now();
    }

    public static HistoricoAlteracao registrarAlteracao(
            Pedido pedido,
            Funcionario funcionario,
            String descricao) {

        return new HistoricoAlteracao(
                pedido,
                funcionario,
                descricao
        );
    }
}
