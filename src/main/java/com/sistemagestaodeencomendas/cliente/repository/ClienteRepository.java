package com.sistemagestaodeencomendas.cliente.repository;

import com.sistemagestaodeencomendas.cliente.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
}
