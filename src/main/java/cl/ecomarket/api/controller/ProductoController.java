package cl.ecomarket.api.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.ecomarket.api.model.Producto;
import cl.ecomarket.api.services.ProductoService;

@RestController
@RequestMapping("/api/v1/productos")
@Tag(name = "Productos", description = "Operaciones relacionadas con los productos")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping
    @Operation(summary = "Listar todos los productos", description = "Obtiene una lista de todos los productos disponibles")
    public ResponseEntity<List<Producto>> listarTodos() {
       List<Producto> productos = productoService.obtenerTodos();
       if (productos.isEmpty()){
        return ResponseEntity.noContent().build();
       }
       return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Obtiene un producto especifico por su ID")
    public ResponseEntity<Producto> obtenerPorProductoId(@PathVariable Long id) {
        try {
            Producto producto = productoService.obtenerPorId(id);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Guardar un nuevo producto", description = "Crea un nuevo producto en el sistema")
    public ResponseEntity<Producto> guardarProducto(@RequestBody Producto producto) {
        Producto nuevoProducto = productoService.agregarProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoProducto);
    }

    @PatchMapping
    @Operation(summary = "Actualizar precio de un producto", description = "Actualiza el precio de un producto existente")
    public ResponseEntity<Producto> actualizarPrecio(@RequestParam Long ProductoId,@RequestParam double Precio) {
        try {
            Producto producto = productoService.actualizarPrecio(Precio, ProductoId);
            return ResponseEntity.ok(producto);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }
    @DeleteMapping
    @Operation(summary = "Eliminar un producto", description = "Elimina un producto del sistema por su ID")
    public ResponseEntity<?> eliminarProducto(@RequestParam Long ProductoId) {
        try {
            productoService.eliminarProducto(ProductoId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
