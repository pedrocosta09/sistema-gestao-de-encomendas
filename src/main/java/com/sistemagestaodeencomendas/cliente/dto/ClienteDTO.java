package com.sistemagestaodeencomendas.cliente.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.br.CPF;

public record ClienteDTO(

        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O CPF é obrigatório")
        @CPF(message = "O CPF informado é inválido")
        String cpf,

        @NotBlank(message = "O email é obrigatório")
        @Email(message = "O email informado é inválido")
        String email

) {
}
