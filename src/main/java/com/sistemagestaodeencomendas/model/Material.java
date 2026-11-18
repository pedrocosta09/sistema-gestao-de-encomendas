
package com.sistemagestaodeencomendas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "materiais")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Material {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome do material é obrigatório")
    @Column(nullable = false)
    private String nome;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UnidadeMedida unidadeMedida;

    @Column(nullable = false, precision = 15, scale = 3)
    private BigDecimal quantidadeEstoque = BigDecimal.ZERO;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal custoUnitario;

    public Material(
            String nome,
            UnidadeMedida unidadeMedida,
            BigDecimal custoUnitario) {

        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome do material é obrigatório."
            );
        }

        if (unidadeMedida == null) {
            throw new IllegalArgumentException(
                    "A unidade de medida é obrigatória."
            );
        }

        this.nome = nome.trim();
        this.unidadeMedida = unidadeMedida;
        this.quantidadeEstoque = BigDecimal.ZERO;

        atualizarCusto(custoUnitario);
    }

    public void registrarEntrada(BigDecimal quantidade) {
        validarQuantidade(quantidade);

        this.quantidadeEstoque =
                this.quantidadeEstoque.add(quantidade);
    }

    public void registrarSaida(BigDecimal quantidade) {
        validarQuantidade(quantidade);

        if (this.quantidadeEstoque.compareTo(quantidade) < 0) {
            throw new IllegalStateException(
                    "Estoque insuficiente para realizar a saída."
            );
        }

        this.quantidadeEstoque =
                this.quantidadeEstoque.subtract(quantidade);
    }

    public void atualizarCusto(BigDecimal novoCusto) {
        if (novoCusto == null ||
                novoCusto.compareTo(BigDecimal.ZERO) < 0) {

            throw new IllegalArgumentException(
                    "O custo não pode ser negativo."
            );
        }

        this.custoUnitario = novoCusto;
    }

    private void validarQuantidade(BigDecimal quantidade) {
        if (quantidade == null ||
                quantidade.compareTo(BigDecimal.ZERO) <= 0) {

            throw new IllegalArgumentException(
                    "A quantidade deve ser maior que zero."
            );
        }
    }
}
