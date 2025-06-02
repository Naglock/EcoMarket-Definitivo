package cl.ecomarket.api.repository;

import cl.ecomarket.api.model.Venta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface VentaRepository extends JpaRepository<Venta, Long>{
    List<Venta> findByPedidoClienteId(Long clienteId);
}
