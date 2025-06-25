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


@RestController
@RequestMapping("/api/v1/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;

    
    @GetMapping("/tiendas")
    public ResponseEntity<List<Inventario>> listarTodos(){
        List<Inventario> inventarios = inventarioService.listarTodos();
        if (inventarios.isEmpty()){
            return ResponseEntity.noContent().build(); // Si la lista es vacia, da un estado de noContent()
        }
        return ResponseEntity.ok(inventarios); // Si hay items da un ok
    }

    @GetMapping("/tienda/{TiendaId}")
    public ResponseEntity<List<ProductoInventariado>> listarPorIdTienda(@PathVariable Long TiendaId) {
        try {
            Inventario inventario = inventarioService.encontrarPorTienda(TiendaId);
            return ResponseEntity.ok(inventario.getProductosInventariados()); // Retorna los productos del inventario de la tienda
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Si no se encuentra el inventario, retorna notFound
        }   
    }

    @PostMapping
    public ResponseEntity<Inventario> guardarInventario(@RequestBody Inventario inventario) {
        try {
            Inventario nuevoInventario = inventarioService.crearInventario(inventario);
            return ResponseEntity.status(HttpStatus.CREATED).body(nuevoInventario); // Retorna un estado CREATED con el inventario creado           
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Si hay un error, retorna un estado BAD_REQUEST
        }
    }
    
    @PatchMapping
    public ResponseEntity<Inventario> actualizarStock(@RequestParam Long tiendaId,@RequestParam Long productoId,@RequestParam String operacion, @RequestParam int stock){ // operacion = ["agregar","quitar"]
        try {
            Inventario inventario = inventarioService.actualizarStock(tiendaId, productoId, stock, operacion);
            return ResponseEntity.ok(inventario); // Retorna un ok con el inventario actualizado
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build(); // Si hay un error, retorna un estado BAD_REQUEST
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarInventario(@PathVariable Long id) {
        try {
            inventarioService.eliminarInventario(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

}
