package cl.ecomarket.api.services;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.ecomarket.api.model.Producto;
import cl.ecomarket.api.repository.ProductoRepository;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Producto agregarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public Producto obtenerPorId(Long id) throws Exception {
        if (!productoRepository.existsById(id)) {
            throw new Exception("Producto no encontrado");
        }
        return productoRepository.findById(id).get();
    }

    public void eliminarProducto(Long id) throws Exception {
        if (!productoRepository.existsById(id)) {
            throw new Exception("Producto no encontrado");
        }
        productoRepository.deleteById(id);
    }

    public Producto actualizarPrecio (double precio, Long productoId) throws Exception {
        Producto producto = productoRepository.findById(productoId).orElseThrow(() -> new Exception("Producto no encontrado"));
        if (precio <= 0) {
            throw new Exception("El precio debe ser mayor a cero");
        }
        producto.setPrecio(precio);
        return productoRepository.save(producto);
    }   

}
