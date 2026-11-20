package com.sistemagestaodeencomendas.cliente.mapper;

import com.sistemagestaodeencomendas.cliente.dto.ClienteDTO;
import com.sistemagestaodeencomendas.cliente.entity.Cliente;
import org.springframework.stereotype.Component;

@Component
public class ClienteMapper {

    public Cliente toEntity(ClienteDTO dto) {
        return new Cliente(
                dto.nome(),
                dto.cpf(),
                dto.email()
        );
    }

    public ClienteDTO toDTO(Cliente cliente) {
        return new ClienteDTO(
                cliente.getNome(),
                cliente.getCpf(),
                cliente.getEmail()
        );
    }
}
