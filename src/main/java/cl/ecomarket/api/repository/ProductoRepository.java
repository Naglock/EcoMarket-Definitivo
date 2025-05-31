package cl.ecomarket.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import cl.ecomarket.api.model.Producto;

public interface  ProductoRepository extends JpaRepository<Producto, Integer>  {}
