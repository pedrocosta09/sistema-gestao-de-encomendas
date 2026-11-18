
package com.sistemagestaodeencomendas.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;

@Entity
@Table(name = "clientes")
@Getter
@NoArgsConstructor(access = lombok.AccessLevel.PROTECTED)
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O nome é obrigatório")
    @Column(nullable = false)
    private String nome;

    @NotBlank(message = "O CPF é obrigatório")
    @CPF(message = "O CPF informado é inválido")
    @Size(min = 11, max = 14)
    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @NotBlank(message = "O email é obrigatório")
    @Email(message = "O email informado é inválido")
    @Column(nullable = false)
    private String email;

    public Cliente(String nome, String cpf, String email) {
        atualizarContato(nome, cpf);
        atualizarEmail(email);
    }

    public void atualizarContato(String novoNome, String novoCpf) {
        if (novoNome == null || novoNome.isBlank()) {
            throw new IllegalArgumentException(
                    "O nome não pode estar vazio."
            );
        }

        if (novoCpf == null || novoCpf.isBlank()) {
            throw new IllegalArgumentException(
                    "O CPF não pode estar vazio."
            );
        }

        this.nome = novoNome.trim();
        this.cpf = novoCpf.trim();
    }

    public void atualizarEmail(String novoEmail) {
        if (novoEmail == null || novoEmail.isBlank()) {
            throw new IllegalArgumentException(
                    "O email não pode estar vazio."
            );
        }

        String emailNormalizado = novoEmail.trim();

        if (!emailNormalizado.matches(
                "^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
            throw new IllegalArgumentException(
                    "O email informado é inválido."
            );
        }

        this.email = emailNormalizado;
    }
}
