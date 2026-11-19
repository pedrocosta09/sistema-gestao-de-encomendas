
package com.sistemagestaodeencomendas.funcionario.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

@Entity
@Table(name = "funcionarios")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Funcionario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "O CPF é obrigatório")
    @CPF(message = "O CPF informado é inválido")
    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @NotBlank(message = "O cargo é obrigatório")
    @Column(nullable = false)
    private String cargo;

    @Column(nullable = false)
    private boolean ativo = true;

    public Funcionario(String nome, String cpf, String cargo) {
        atualizarContato(nome, cpf);
        alterarCargo(cargo);
        this.ativo = true;
    }

    public void atualizarContato(String nome, String cpf) {
        if (nome == null || nome.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome não pode estar vazio."
            );
        }

        if (cpf == null || cpf.isBlank()) {
            throw new IllegalArgumentException(
                    "O CPF não pode estar vazio."
            );
        }

        this.nome = nome.trim();
        this.cpf = cpf.trim();
    }

    public void alterarCargo(String novoCargo) {
        if (novoCargo == null || novoCargo.isBlank()) {
            throw new IllegalArgumentException(
                    "O cargo não pode estar vazio."
            );
        }

        this.cargo = novoCargo.trim();
    }

    public void desativar() {
        if (!ativo) {
            throw new IllegalStateException(
                    "O funcionário já está desativado."
            );
        }

        this.ativo = false;
    }

    public void reativar() {
        if (ativo) {
            throw new IllegalStateException(
                    "O funcionário já está ativo."
            );
        }

        this.ativo = true;
    }
}
