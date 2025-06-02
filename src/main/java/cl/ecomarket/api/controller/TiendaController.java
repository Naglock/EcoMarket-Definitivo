package cl.ecomarket.api.controller;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import cl.ecomarket.api.model.Tienda;
import cl.ecomarket.api.services.TiendaService;
@RestController
@RequestMapping("/api/v1/tiendas")
public class TiendaController {

    @Autowired
    private TiendaService tiendaService;

    @GetMapping
    public ResponseEntity<List<Tienda>> obtenerTodasLasTiendas() {
        return ResponseEntity.ok(tiendaService.obtenerTodasLasTiendas()); // Hay que cambiar la respuesta entregada
    }                                                                     // cuando este vacio, debe decir NoContent (pendiente)
    @GetMapping("/{id}")
    public ResponseEntity<Tienda> obtenerTiendaPorId(@PathVariable Long id) {
        try {
            Tienda tienda = tiendaService.obtenerTiendaPorId(id); 
            return ResponseEntity.ok(tienda);
        } catch(Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    public ResponseEntity<Tienda> crearTienda(@RequestBody Tienda tienda) {
        return ResponseEntity.ok(tiendaService.guardarTienda(tienda)); // Que genere un status CREATED().body(tienda) (pendiente)
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTienda(@PathVariable Long id) {
        tiendaService.eliminarTienda(id);
        return ResponseEntity.noContent().build();
    }

}