package cl.ecomarket.api.services;

import java.util.List;
import java.util.Optional;
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
        public Producto obtenerPorId(Long id) {
        return productoRepository.findById(id).get();
    }

    public Producto actualizaPrecio(Long productoId, double nuevoPrecio) throws Exception{
        if(nuevoPrecio <= 0){
            throw new IllegalArgumentException("El precio debe ser mayor a 0");
        }
        Producto producto = productoRepository.findById(productoId)
            .orElseThrow(() -> new Exception("Producto no encontrado"));
        producto.setPrecio(nuevoPrecio);
        return productoRepository.save(producto);
    }

    public void eliminarProducto(Long id) {
        Optional<Producto> producto = productoRepository.findById(id);
        if (producto.isPresent()) {
        } else {
            throw new RuntimeException("Producto no encontrado con ID: " + id);
        }
        productoRepository.deleteById(id);
    }


}
