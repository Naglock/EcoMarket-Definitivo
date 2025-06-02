package cl.ecomarket.api.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.*;

import cl.ecomarket.api.model.Producto;
import cl.ecomarket.api.services.ProductoService;

@Service
@RestController
@RequestMapping("/api/v1/productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<Producto>> listarTodos() {
       List<Producto> productos = productoService.obtenerTodos();
       if (productos.isEmpty()){
        return ResponseEntity.noContent().build();
       }
       return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Producto> obtenerPorProductoId(@PathVariable Long id) {
        try {
            Producto producto = productoService.obtenerPorId(id);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Producto> guardarProducto(@RequestBody Producto producto) {
        Producto nuevoProducto = productoService.agregarProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    @PatchMapping
    public ResponseEntity<Producto> actualizarPrecio(@RequestParam Long ProductoId,@RequestParam double Precio) {
        try {
            Producto producto = productoService.obtenerPorId(ProductoId);
            if (Precio<=0){
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            } else {
                producto.setPrecio(Precio);
                productoService.agregarProducto(producto);
                return ResponseEntity.ok(producto);
            }
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping
    public ResponseEntity<?> eliminarProducto(@RequestParam Long ProductoId) {
        try {
            productoService.eliminarProducto(ProductoId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
