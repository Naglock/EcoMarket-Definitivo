package cl.ecomarket.api.services;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.ecomarket.api.model.Inventario;
import cl.ecomarket.api.model.ProductoInventariado;
import cl.ecomarket.api.repository.InventarioRepository;
import cl.ecomarket.api.repository.ProductoRepository;
import cl.ecomarket.api.repository.TiendaRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class InventarioService {

    @Autowired
    private InventarioRepository inventarioRepository;
    @Autowired
    private ProductoRepository productoRepository;
    @Autowired
    private TiendaRepository tiendaRepository;

    public List<Inventario> listarInventarioPorTiendaId(Long TiendaId) throws Exception {
        // verificar si la tienda existe
        if (!tiendaRepository.existsById(TiendaId)) {
            throw new Exception("La tienda con ID " + TiendaId + " no existe");
        }
        return inventarioRepository.findByTiendaId(TiendaId);
    }

    public Inventario crearInventario(Inventario inventario) throws Exception {
        // verificar si la tienda existe
        if (inventario.getTienda() == null || !tiendaRepository.existsById(inventario.getTienda().getId())) {
            throw new Exception("La tienda no existe");
        }
        // verificar si los productos existen
        for (ProductoInventariado productoInventariado : inventario.getProductosInventariados()) {
            if (productoInventariado.getProducto() == null || !productoRepository.existsById(productoInventariado.getProducto().getId())) {
                throw new Exception("El producto con ID " + productoInventariado.getProducto().getId() + " no existe");
            }
        }
        // si se verifican las condiciones, se establece la tienda y los productos en el inventario
        inventario.setTienda(tiendaRepository.findById(inventario.getTienda().getId()).get());
        for (ProductoInventariado productoInventariado : inventario.getProductosInventariados()) {
            productoInventariado.setProducto(productoRepository.findById(productoInventariado.getProducto().getId()).get());
            if (productoInventariado.getStock() == null) {
                productoInventariado.setStock(0); // Si el stock es nulo, se establece en 0
            }   
        }
        // guardar el inventario en la base de datos
        return inventarioRepository.save(inventario);
    }

    public void eliminarInventario(Long InventarioId) throws Exception {
        // verificar si el inventario existe
        if (!inventarioRepository.existsById(InventarioId)) {
            throw new Exception("El inventario con ID " + InventarioId + " no existe");
        }
        // eliminar el inventario
        inventarioRepository.deleteById(InventarioId);
    }

    public List<Inventario> listarTodos() {
        return inventarioRepository.findAll();
    }

    public Inventario encontrarPorId(Long idInventario) throws RuntimeException {
        // verificar si el inventario existe
        Optional<Inventario> inventarioOpt = inventarioRepository.findById(idInventario);
        if (inventarioOpt.isPresent()) {
            return inventarioOpt.get();
        } else {
            throw new RuntimeException("El inventario con ID " + idInventario + " no existe");
        }
    }

    public Inventario encontrarPorTienda(Long tiendaId) throws RuntimeException {
        // verificar si la tienda existe
        if (!tiendaRepository.existsById(tiendaId)) {
            throw new RuntimeException("La tienda con ID " + tiendaId + " no existe");
        }
        // buscar el inventario por la tienda
        return inventarioRepository.findByTiendaId(tiendaId)
                .stream()
                .findFirst()
                .orElseThrow(() -> new RuntimeException("No se encontró inventario para la tienda con ID " + tiendaId));
    }

    public Inventario actualizarStock(Long tiendaId, Long productoId, int cantidad, String operacion) throws Exception {
        // verificar si el inventario existe
        Inventario inventario = encontrarPorTienda(tiendaId);
        // buscar el producto en el inventario
        ProductoInventariado productoInventariado = inventario.getProductosInventariados()
                .stream()
                .filter(prodInv -> prodInv.getProducto().getId().equals(productoId))
                .findFirst()
                .orElseThrow(() -> new Exception("El producto con ID " + productoId + " no está en el inventario"));

        // actualizar stock según la operación
        if (operacion.equals("agregar")) {
            productoInventariado.setStock(productoInventariado.getStock() + cantidad);
        } else if (operacion.equals("quitar")) {
            if (productoInventariado.getStock() < cantidad) {
                throw new Exception("No hay suficiente stock para quitar");
            }
            productoInventariado.setStock(productoInventariado.getStock() - cantidad);
        } else {
            throw new Exception("Operación no válida. Use 'agregar' o 'quitar'");
        }

        // guardar los cambios en el inventario
        return inventarioRepository.save(inventario);
    }
    
    // verifica si hay stock disponible para un producto en una tienda específica
    public boolean hayStock(Long productoId, Long tiendaId, int cantidad) throws RuntimeException {
        try {
            Inventario inventario = encontrarPorTienda(tiendaId);
            ProductoInventariado producto = inventario.getProductosInventariados()
                    .stream()
                    .filter(prodInv -> prodInv.getProducto().getId().equals(productoId))
                    .findFirst()
                    .orElse(null);
            return producto != null && producto.getStock() >= cantidad;
        } catch (Exception e) {
            return false; // Si no se encuentra el inventario o el producto, no hay stock
        }
    }
}
