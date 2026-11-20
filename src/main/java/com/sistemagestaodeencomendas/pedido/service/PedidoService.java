package com.sistemagestaodeencomendas.pedido.service;

import com.sistemagestaodeencomendas.cliente.entity.Cliente;
import com.sistemagestaodeencomendas.cliente.repository.ClienteRepository;
import com.sistemagestaodeencomendas.itempedido.entity.ItemPedido;
import com.sistemagestaodeencomendas.pedido.dto.ItemPedidoRequestDTO;
import com.sistemagestaodeencomendas.pedido.dto.PedidoRequestDTO;
import com.sistemagestaodeencomendas.pedido.dto.PedidoResponseDTO;
import com.sistemagestaodeencomendas.pedido.entity.Pedido;
import com.sistemagestaodeencomendas.pedido.mapper.PedidoMapper;
import com.sistemagestaodeencomendas.pedido.repository.PedidoRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;

import java.util.List;

@Service
@Validated
public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ClienteRepository clienteRepository;
    private final PedidoMapper pedidoMapper;

    public PedidoService(
            PedidoRepository pedidoRepository,
            ClienteRepository clienteRepository,
            PedidoMapper pedidoMapper) {
        this.pedidoRepository = pedidoRepository;
        this.clienteRepository = clienteRepository;
        this.pedidoMapper = pedidoMapper;
    }

    @Transactional
    public PedidoResponseDTO cadastrar(@Valid PedidoRequestDTO dados) {
        Cliente cliente = clienteRepository.findById(dados.clienteId())
                .orElseThrow(() -> new EntityNotFoundException(
                        "Cliente não encontrado: " + dados.clienteId()));

        Pedido pedido = new Pedido(cliente, dados.dataEntrega());

        for (ItemPedidoRequestDTO itemDTO : dados.itens()) {
            ItemPedido item = pedidoMapper.toEntity(itemDTO);
            pedido.adicionarItem(item);
        }

        Pedido pedidoSalvo = pedidoRepository.save(pedido);
        return pedidoMapper.toResponseDTO(pedidoSalvo);
    }

    @Transactional(readOnly = true)
    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAll()
                .stream()
                .map(pedidoMapper::toResponseDTO)
                .toList();
    }

    @Transactional(readOnly = true)
    public PedidoResponseDTO buscarPorId(Long id) {
        Pedido pedido = pedidoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Pedido não encontrado: " + id));
        return pedidoMapper.toResponseDTO(pedido);
    }
}
