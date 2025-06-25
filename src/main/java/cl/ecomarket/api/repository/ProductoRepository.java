package cl.ecomarket.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.ecomarket.api.model.Producto;

@Repository
public interface  ProductoRepository extends JpaRepository<Producto, Long>  {
}
