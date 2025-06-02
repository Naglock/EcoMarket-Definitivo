package cl.ecomarket.api.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.ecomarket.api.model.Inventario;
import cl.ecomarket.api.repository.InventarioRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository ;

    public List<Inventario> listarInventarioPorTiendaId(Long TiendaId) {
        return inventarioRepository.findByTiendaId(TiendaId);
    }

    public Inventario guardarInventario(Inventario inventario) {
        return inventarioRepository.save(inventario);
    }

    public void eliminarInventario(Long InventarioId) {
        inventarioRepository.deleteById(InventarioId);
    }

    public List<Inventario> listarTodos() {
        return inventarioRepository.findAll();
    }

    public Inventario encontrarPorId(Long idInventario) {
        return inventarioRepository.findById(idInventario).get();
    }

    public boolean hayStock(Long productoId, Long tiendaId, int cantidadSolicitada) {
        Optional<Inventario> inventarioOpt = inventarioRepository.findByProductoIdAndTiendaId(productoId, tiendaId);
        return inventarioOpt.isPresent() && inventarioOpt.get().getStock() >= cantidadSolicitada;
    }
    
    public Inventario encontrarPorProductoYTienda(Long productoId, Long tiendaId) {
        return inventarioRepository.findByProductoIdAndTiendaId(productoId, tiendaId).get();
    }


}
