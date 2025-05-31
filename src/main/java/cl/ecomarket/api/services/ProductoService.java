package cl.ecomarket.api.services;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import cl.ecomarket.api.model.Producto;
import cl.ecomarket.api.repository.ProductoRepository;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepository productoRepository;

    public List<Producto> obtenerTodos() {
        return productoRepository.findAll();
    }

    public Producto agregarProducto(Producto producto) {
        return productoRepository.save(producto);
    }

    public Optional<Producto> obtenerPorId(int id) {
        return productoRepository.findById(id);
    }

    public Optional<Producto> actualizarProducto(int id, Producto nuevoProducto) {
        return productoRepository.findById(id).map(producto -> {
            producto.setNombre(nuevoProducto.getNombre());
            producto.setDescripcion(nuevoProducto.getDescripcion());
            producto.setStock(nuevoProducto.getStock());
            producto.setPrecio(nuevoProducto.getPrecio());
            return productoRepository.save(producto);
        });
    }

    public boolean eliminarProducto(int id) {
        if (productoRepository.existsById(id)) {
            productoRepository.deleteById(id);
            return true;
        }
        return false;
    }

    public Optional<Producto> actualizarStock(int id, int cantidad) {
        return productoRepository.findById(id).map(producto -> {
            producto.setStock(producto.getStock() + cantidad);
            return productoRepository.save(producto);
        });
    }
}
