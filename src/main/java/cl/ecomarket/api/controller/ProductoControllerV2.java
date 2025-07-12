package cl.ecomarket.api.controller;

import cl.ecomarket.api.assemblers.ProductoModelAssembler;
import cl.ecomarket.api.model.Producto;
import cl.ecomarket.api.services.ProductoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/productos")
@Tag(name = "Productos V2", description = "Operaciones HATEOAS relacionadas con los productos")
public class ProductoControllerV2 {

    @Autowired
    private ProductoService productoService;

    @Autowired
    private ProductoModelAssembler assembler;

    @GetMapping
    @Operation(summary = "Listar todos los productos", description = "Obtiene una lista de todos los productos disponibles con enlaces HATEOAS")
    public ResponseEntity<CollectionModel<EntityModel<Producto>>> listarTodos() {
        List<Producto> productos = productoService.obtenerTodos();

        if (productos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Producto>> productosModel = productos.stream()
                .map(assembler::toModel)
                .collect(Collectors.toList());

        return ResponseEntity.ok(
                CollectionModel.of(productosModel,
                        linkTo(methodOn(ProductoControllerV2.class).listarTodos()).withSelfRel())
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener producto por ID", description = "Obtiene un producto específico por su ID con enlaces HATEOAS")
    public ResponseEntity<EntityModel<Producto>> obtenerPorProductoId(@PathVariable Long id) {
        try {
            Producto producto = productoService.obtenerPorId(id);
            return ResponseEntity.ok(assembler.toModel(producto));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Guardar un nuevo producto", description = "Crea un nuevo producto con enlaces HATEOAS")
    public ResponseEntity<EntityModel<Producto>> guardarProducto(@RequestBody Producto producto) {
        Producto nuevoProducto = productoService.agregarProducto(producto);
        return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(nuevoProducto));
    }

    @PatchMapping
    @Operation(summary = "Actualizar precio de un producto", description = "Actualiza el precio de un producto con enlaces HATEOAS")
    public ResponseEntity<EntityModel<Producto>> actualizarPrecio(@RequestParam Long ProductoId, @RequestParam double Precio) {
        try {
            Producto producto = productoService.actualizarPrecio(Precio, ProductoId);
            return ResponseEntity.ok(assembler.toModel(producto));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @DeleteMapping
    @Operation(summary = "Eliminar un producto", description = "Elimina un producto del sistema por su ID")
    public ResponseEntity<Void> eliminarProducto(@RequestParam Long ProductoId) {
        try {
            productoService.eliminarProducto(ProductoId);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
