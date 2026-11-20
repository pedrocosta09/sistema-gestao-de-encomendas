package com.sistemagestaodeencomendas.cliente.controller;

import com.sistemagestaodeencomendas.cliente.dto.ClienteDTO;
import com.sistemagestaodeencomendas.cliente.service.ClienteService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
public class ClienteController {

    private final ClienteService clienteService;

    public ClienteController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteDTO cadastrar(@Valid @RequestBody ClienteDTO dto) {
        return clienteService.cadastrar(dto);
    }

    @GetMapping
    public List<ClienteDTO> listar() {
        return clienteService.listar();
    }
}
