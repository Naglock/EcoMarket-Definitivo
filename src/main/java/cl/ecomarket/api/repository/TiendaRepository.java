package cl.ecomarket.api.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import cl.ecomarket.api.model.Tienda;

@Repository
public interface TiendaRepository extends JpaRepository<Tienda, Long> {
}
