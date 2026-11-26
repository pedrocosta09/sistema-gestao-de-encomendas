package com.sistemagestaodeencomendas.cliente.service;

import com.sistemagestaodeencomendas.cliente.dto.ClienteDTO;
import com.sistemagestaodeencomendas.cliente.entity.Cliente;
import com.sistemagestaodeencomendas.cliente.mapper.ClienteMapper;
import com.sistemagestaodeencomendas.cliente.repository.ClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final ClienteMapper clienteMapper;

    public ClienteService(
            ClienteRepository clienteRepository,
            ClienteMapper clienteMapper) {

        this.clienteRepository = clienteRepository;
        this.clienteMapper = clienteMapper;
    }

    public ClienteDTO cadastrar(ClienteDTO dto) {
        Cliente cliente = clienteMapper.toEntity(dto);

        Cliente clienteSalvo = clienteRepository.save(cliente);

        return clienteMapper.toDTO(clienteSalvo);
    }

    public List<ClienteDTO> listar() {
        return clienteRepository.findAll()
                .stream()
                .map(clienteMapper::toDTO)
                .toList();
    }
}
