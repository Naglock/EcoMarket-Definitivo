package cl.ecomarket.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.ecomarket.api.model.Inventario;
import cl.ecomarket.api.model.Producto;
import cl.ecomarket.api.model.Tienda;
import cl.ecomarket.api.services.InventarioService;
import cl.ecomarket.api.services.ProductoService;
import cl.ecomarket.api.services.TiendaService;

@RestController
@RequestMapping("/api/v1/inventario")
public class InventarioController {

    @Autowired
    private InventarioService inventarioService;
    @Autowired
    private ProductoService productoService;
    @Autowired
    private TiendaService tiendaService;

    @GetMapping("/tiendas")
    public ResponseEntity<List<Inventario>> listarTodos(){
        List<Inventario> inventarios = inventarioService.listarTodos();
        if (inventarios.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(inventarios);
    }

    @GetMapping("/tienda/{TiendaId}")
    public ResponseEntity<List<Inventario>> listarPorIdTienda(@PathVariable Long TiendaId) {
        List<Inventario> inventario = inventarioService.listarInventarioPorTiendaId(TiendaId);
        if (inventario.isEmpty()){
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(inventario);
    }

    @PostMapping
    public ResponseEntity<Inventario> guardarInventario(@RequestBody Inventario inventario) {
        Producto producto = productoService.obtenerPorId(inventario.getProducto().getId());
        Tienda tienda = tiendaService.obtenerTiendaPorId(inventario.getTienda().getId());
        inventario.setProducto(producto);
        inventario.setTienda(tienda);
        Inventario nuevoInventario = inventarioService.guardarInventario(inventario);
        return ResponseEntity.status(HttpStatus.CREATED).body(nuevoInventario);
    }

    @PatchMapping
    public ResponseEntity<Inventario> actualizarStock(@RequestParam Long inventarioId,@RequestParam String operacion, @RequestParam int stock){ // operacion = ["agregar","quitar"]
        try {
            Inventario inv = inventarioService.encontrarPorId(inventarioId);
            if (operacion.equals("agregar")){
                inv.setStock(inv.getStock()+stock);  // Se obtiene el stock actual, y se le suma el stock del @ReqquestParam
                inventarioService.guardarInventario(inv); // Se actualiza el stock en su repository
                return ResponseEntity.ok(inv);
            } else if (operacion.equals("quitar")) {
                if (inv.getStock() >= stock) {
                    inv.setStock(inv.getStock()-stock); //Si el stock actual es inferior al stock que se quitara, dara un error
                    inventarioService.guardarInventario(inv); //Si se cumple bien la condicion, se le resta el stock solicitado al actual y luego se actualiza su repositorio
                    return ResponseEntity.ok(inv);
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                            .body(null);
                }                
            } else {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
            }             
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
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
