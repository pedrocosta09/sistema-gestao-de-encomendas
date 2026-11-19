
package com.sistemagestaodeencomendas.pedido.entity;

import com.sistemagestaodeencomendas.cliente.entity.Cliente;
import com.sistemagestaodeencomendas.funcionario.entity.Funcionario;
import com.sistemagestaodeencomendas.historicoalteracao.entity.HistoricoAlteracao;
import com.sistemagestaodeencomendas.itempedido.entity.ItemPedido;
import com.sistemagestaodeencomendas.pagamento.entity.Pagamento;
import com.sistemagestaodeencomendas.pedido.enums.StatusPedido;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "pedidos")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Pedido {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dataCriacao;

    private LocalDate dataEntrega;

    @Column(nullable = false, precision = 15, scale = 2)
    private BigDecimal valorTotal = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPedido status = StatusPedido.RECEBIDO;

    @Column(nullable = false)
    private boolean arteAprovada = false;

    private LocalDateTime dataAprovacaoArte;

    @Column(length = 1000)
    private String observacoes;

    @Column(nullable = false)
    private boolean confirmado = false;

    @Column(nullable = false)
    private boolean reavaliacaoPendente = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "funcionario_id")
    private Funcionario funcionario;

    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ItemPedido> itens = new ArrayList<>();

    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL
    )
    private List<Pagamento> pagamentos = new ArrayList<>();

    @OneToMany(
            mappedBy = "pedido",
            cascade = CascadeType.ALL
    )
    private List<HistoricoAlteracao> historicoAlteracoes =
            new ArrayList<>();

    public Pedido(Cliente cliente, LocalDate dataEntrega) {
        if (cliente == null) {
            throw new IllegalArgumentException(
                    "O cliente é obrigatório."
            );
        }

        validarDataEntrega(dataEntrega);

        this.cliente = cliente;
        this.dataEntrega = dataEntrega;
        this.dataCriacao = LocalDateTime.now();
    }

    public void adicionarItem(ItemPedido item) {
        if (item == null) {
            throw new IllegalArgumentException(
                    "O item é obrigatório."
            );
        }

        verificarEdicaoPermitida();

        if (itens.contains(item)) {
            throw new IllegalArgumentException(
                    "O item já pertence ao pedido."
            );
        }

        item.vincularPedido(this);
        itens.add(item);
        recalcularValorTotal();
    }

    public void recalcularValorTotal() {
        if (confirmado) {
            throw new IllegalStateException(
                    "Não é possível recalcular um pedido confirmado."
            );
        }

        BigDecimal total = BigDecimal.ZERO;

        for (ItemPedido item : itens) {
            BigDecimal subtotal = item.getValorUnitario()
                    .multiply(
                            BigDecimal.valueOf(item.getQuantidade())
                    );

            total = total.add(subtotal);
        }

        if (total.compareTo(calcularTotalPago()) < 0) {
            throw new IllegalStateException(
                    "O valor total não pode ser menor que o total pago."
            );
        }

        this.valorTotal = total;
    }

    public void aprovarArte() {
        if (status != StatusPedido.RECEBIDO &&
                status != StatusPedido.AGUARDANDO_APROVACAO) {
            throw new IllegalStateException(
                    "A arte não pode ser aprovada neste estado."
            );
        }

        if (itens.isEmpty()) {
            throw new IllegalStateException(
                    "O pedido precisa possuir pelo menos um item."
            );
        }

        if (reavaliacaoPendente) {
            throw new IllegalStateException(
                    "A reavaliação precisa ser concluída."
            );
        }

        this.arteAprovada = true;
        this.dataAprovacaoArte = LocalDateTime.now();
        this.status = StatusPedido.APROVADO;
    }

    public void confirmarPedido() {
        if (!arteAprovada ||
                status != StatusPedido.APROVADO) {
            throw new IllegalStateException(
                    "A arte precisa estar aprovada."
            );
        }

        if (confirmado) {
            throw new IllegalStateException(
                    "O pedido já está confirmado."
            );
        }

        if (itens.isEmpty() || reavaliacaoPendente) {
            throw new IllegalStateException(
                    "O pedido possui pendências."
            );
        }

        this.confirmado = true;
    }

    public void iniciarProducao() {
        if (!confirmado ||
                !arteAprovada ||
                reavaliacaoPendente ||
                status != StatusPedido.APROVADO) {
            throw new IllegalStateException(
                    "O pedido precisa estar aprovado e confirmado."
            );
        }

        this.status = StatusPedido.EM_PRODUCAO;
    }

    public void concluirProducao() {
        if (status != StatusPedido.EM_PRODUCAO) {
            throw new IllegalStateException(
                    "O pedido não está em produção."
            );
        }

        this.status = StatusPedido.CONCLUIDO;
    }

    public void marcarProntoParaEntrega() {
        if (status != StatusPedido.CONCLUIDO) {
            throw new IllegalStateException(
                    "A produção precisa estar concluída."
            );
        }

        this.status = StatusPedido.PRONTO_PARA_ENTREGA;
    }

    public void registrarEntrega() {
        if (status != StatusPedido.PRONTO_PARA_ENTREGA) {
            throw new IllegalStateException(
                    "O pedido não está pronto para entrega."
            );
        }

        if (calcularTotalPago().compareTo(valorTotal) != 0) {
            throw new IllegalStateException(
                    "O pedido precisa estar totalmente pago."
            );
        }

        this.status = StatusPedido.ENTREGUE;
    }

    public void cancelar() {
        if (status == StatusPedido.ENTREGUE ||
                status == StatusPedido.CANCELADO) {
            throw new IllegalStateException(
                    "O pedido não pode ser cancelado."
            );
        }

        this.status = StatusPedido.CANCELADO;
    }

    public void alterarPrazo(LocalDate novaData) {
        validarDataEntrega(novaData);
        verificarPedidoAberto();

        if (confirmado) {
            throw new IllegalStateException(
                    "O prazo de um pedido confirmado exige reavaliação formal."
            );
        }

        this.dataEntrega = novaData;
    }

    public void atribuirFuncionario(Funcionario funcionario) {
        if (funcionario == null || !funcionario.isAtivo()) {
            throw new IllegalArgumentException(
                    "É necessário um funcionário ativo."
            );
        }

        verificarPedidoAberto();
        this.funcionario = funcionario;
    }

    public void adicionarPagamento(Pagamento pagamento) {
        if (pagamento == null) {
            throw new IllegalArgumentException(
                    "O pagamento é obrigatório."
            );
        }

        verificarPedidoAberto();

        if (pagamentos.contains(pagamento)) {
            throw new IllegalArgumentException(
                    "O pagamento já foi adicionado."
            );
        }

        if (pagamento.getDataPagamento() != null) {
            throw new IllegalStateException(
                    "O pagamento já foi registrado."
            );
        }

        pagamento.vincularPedido(this);
        pagamentos.add(pagamento);
    }

    public void validarNovoPagamento(BigDecimal valor) {
        verificarPedidoAberto();

        if (valor == null ||
                valor.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException(
                    "O pagamento deve ser maior que zero."
            );
        }

        if (calcularTotalPago().add(valor)
                .compareTo(valorTotal) > 0) {
            throw new IllegalStateException(
                    "O pagamento ultrapassa o valor total do pedido."
            );
        }
    }

    public BigDecimal calcularTotalPago() {
        BigDecimal total = BigDecimal.ZERO;

        for (Pagamento pagamento : pagamentos) {
            if (pagamento.getDataPagamento() != null &&
                    !pagamento.isEstornado()) {
                total = total.add(pagamento.getValor());
            }
        }

        return total;
    }

    public void registrarAlteracao(
            Funcionario funcionario,
            String descricao) {

        verificarPedidoAberto();
        validarFuncionario(funcionario);

        HistoricoAlteracao registro =
                HistoricoAlteracao.registrarAlteracao(
                        this,
                        funcionario,
                        descricao
                );

        historicoAlteracoes.add(registro);
    }

    public void solicitarReavaliacao(
            Funcionario funcionario,
            String justificativa) {

        validarFuncionario(funcionario);

        if (!confirmado ||
                status != StatusPedido.APROVADO) {
            throw new IllegalStateException(
                    "A reavaliação exige um pedido confirmado e ainda não iniciado."
            );
        }

        if (reavaliacaoPendente) {
            throw new IllegalStateException(
                    "Já existe uma reavaliação pendente."
            );
        }

        if (justificativa == null || justificativa.isBlank()) {
            throw new IllegalArgumentException(
                    "A justificativa é obrigatória."
            );
        }

        registrarAlteracao(
                funcionario,
                "Reavaliação solicitada: " + justificativa.trim()
        );

        this.reavaliacaoPendente = true;
        this.arteAprovada = false;
        this.dataAprovacaoArte = null;
        this.confirmado = false;
        this.status = StatusPedido.AGUARDANDO_APROVACAO;
    }

    public void concluirReavaliacao(
            Funcionario funcionario,
            LocalDate novoPrazo,
            String justificativaCustoPrazo) {

        validarFuncionario(funcionario);

        if (!reavaliacaoPendente ||
                status != StatusPedido.AGUARDANDO_APROVACAO) {
            throw new IllegalStateException(
                    "Não existe reavaliação pendente."
            );
        }

        validarDataEntrega(novoPrazo);

        if (justificativaCustoPrazo == null ||
                justificativaCustoPrazo.isBlank()) {
            throw new IllegalArgumentException(
                    "A justificativa de custo e prazo é obrigatória."
            );
        }

        BigDecimal novoTotal = BigDecimal.ZERO;

        for (ItemPedido item : itens) {
            novoTotal = novoTotal.add(
                    item.getValorUnitario().multiply(
                            BigDecimal.valueOf(item.getQuantidade())
                    )
            );
        }

        if (novoTotal.compareTo(calcularTotalPago()) < 0) {
            throw new IllegalStateException(
                    "O novo valor não pode ser menor que o total pago."
            );
        }

        registrarAlteracao(
                funcionario,
                "Reavaliação de custo e prazo: " +
                        justificativaCustoPrazo.trim() +
                        ". Novo prazo: " + novoPrazo +
                        ". Novo valor total: " + novoTotal
        );

        this.dataEntrega = novoPrazo;
        this.valorTotal = novoTotal;
        this.reavaliacaoPendente = false;

        // Exige nova aprovação e confirmação.
        this.arteAprovada = false;
        this.confirmado = false;
        this.status = StatusPedido.AGUARDANDO_APROVACAO;
    }

    private void verificarEdicaoPermitida() {
        verificarPedidoAberto();

        if (arteAprovada || confirmado) {
            throw new IllegalStateException(
                    "Não é possível alterar os itens após a aprovação."
            );
        }
    }

    private void verificarPedidoAberto() {
        if (status == StatusPedido.ENTREGUE ||
                status == StatusPedido.CANCELADO) {
            throw new IllegalStateException(
                    "O pedido está encerrado."
            );
        }
    }

    private void validarFuncionario(Funcionario funcionario) {
        if (funcionario == null || !funcionario.isAtivo()) {
            throw new IllegalArgumentException(
                    "É necessário um funcionário ativo."
            );
        }
    }

    private void validarDataEntrega(LocalDate data) {
        if (data == null || data.isBefore(LocalDate.now())) {
            throw new IllegalArgumentException(
                    "A data de entrega é inválida."
            );
        }
    }
}
