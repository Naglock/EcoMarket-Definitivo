package cl.ecomarket.api.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.ecomarket.api.model.Inventario;

@Repository
public interface InventarioRepository extends JpaRepository <Inventario, Long> {
    List<Inventario> findByTiendaId(Long TiendaId);
    Optional<Inventario> findByProductoIdAndTiendaId(Long productoId,Long tiendaId);


}
