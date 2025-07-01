package cl.ecomarket.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import cl.ecomarket.api.model.Inventario;
import cl.ecomarket.api.model.ProductoInventariado;
import cl.ecomarket.api.services.InventarioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;


@RestController
@RequestMapping("/api/v1/inventario")
@Tag(name = "Inventario", description = "Operaciones relacionadas con el inventario de productos en las tiendas")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    
    @GetMapping("/tiendas")
    @Operation(summary = "Listar todos los inventarios", description = "Obtiene una lista de todos los inventarios de productos en las tiendas")
    public ResponseEntity<List<Inventario>> listarTodos(){
        List<Inventario> inventarios = inventarioService.listarTodos();
        if (inventarios.isEmpty()){
            return ResponseEntity.noContent().build(); // Si la lista es vacia, da un estado de noContent()
        }
        return ResponseEntity.ok(inventarios); // Si hay items da un ok
    }

    @GetMapping("/tienda/{TiendaId}")
    @Operation(summary = "Listar productos por ID de tienda", description = "Obtiene una lista de productos inventariados en una tienda específica")
    public ResponseEntity<List<ProductoInventariado>> listarPorIdTienda(@PathVariable Long TiendaId) {
        try {
            Inventario inventario = inventarioService.encontrarPorTienda(TiendaId);
            return ResponseEntity.ok(inventario.getProductosInventariados()); // Retorna los productos del inventario de la tienda
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Si no se encuentra el inventario, retorna notFound
        }
    }

    @PostMapping
    @Operation(summary = "Guardar un nuevo inventario", description = "Crea un nuevo inventario de productos en una tienda")
    public ResponseEntity<Inventario> guardarInventario(@RequestBody Inventario inventario) {
        try {
            Inventario nuevoInventario = inventarioService.crearInventario(inventario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoInventario); // Retorna un estado CREATED con el inventario creado           
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Si hay un error, retorna un estado BAD_REQUEST
        }
    }
    
    @PatchMapping
    @Operation(summary = "Actualizar stock de un producto en una tienda", description = "Actualiza el stock de un producto específico en una tienda")
    public ResponseEntity<Inventario> actualizarStock(@RequestParam Long tiendaId,@RequestParam Long productoId,@RequestParam String operacion, @RequestParam int stock){ // operacion = ["agregar","quitar"]
        try {
            Inventario inventario = inventarioService.actualizarStock(tiendaId, productoId, stock, operacion);
            return ResponseEntity.ok(inventario); // Retorna un ok con el inventario actualizado
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Si hay un error, retorna un estado BAD_REQUEST
        }
    }

    @DeleteMapping("/eliminar/{id}")
    @Operation(summary = "Eliminar un inventario por ID", description = "Elimina un inventario específico por su ID")
    public ResponseEntity<?> eliminarInventario(@PathVariable Long id) {
        try {
            inventarioService.eliminarInventario(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
