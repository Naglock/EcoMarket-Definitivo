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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import cl.ecomarket.api.model.Tienda;
import cl.ecomarket.api.services.TiendaService;
@RestController
@RequestMapping("/api/v1/tiendas")
@Tag(name = "Tiendas", description = "Operaciones relacionadas con las tiendas")
public class TiendaController {

    @Autowired
    private TiendaService tiendaService;

    @GetMapping
    @Operation(summary = "Obtener todas las tiendas", description = "Retorna una lista de todas las tiendas disponibles")
    public ResponseEntity<List<Tienda>> obtenerTodasLasTiendas() {
        try {
            List<Tienda> tiendas = tiendaService.obtenerTodasLasTiendas();
            if (tiendas.isEmpty()) {
                return ResponseEntity.noContent().build(); // Retorna 204 No Content si no hay tiendas
            }
            return ResponseEntity.ok(tiendas); // Retorna 200 OK con la lista de tiendas
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // Retorna 500 Internal Server Error si ocurre un error
        }
    }
    @GetMapping("/{id}")
    @Operation(summary = "Obtener tienda por ID", description = "Retorna una tienda específica por su ID")
    public ResponseEntity<Tienda> obtenerTiendaPorId(@PathVariable Long id) {
        try {
            Tienda tienda = tiendaService.obtenerTiendaPorId(id);
            return ResponseEntity.ok(tienda);
        } catch(Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear una nueva tienda", description = "Crea una nueva tienda en el sistema")
    public ResponseEntity<Tienda> crearTienda(@RequestBody Tienda tienda) {
        try {
            Tienda nuevaTienda = tiendaService.guardarTienda(tienda);
            return ResponseEntity.status(201).body(nuevaTienda); // Retorna 201 Created con la tienda creada
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // Retorna 400 Bad Request si hay un error
        }
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar tienda por ID", description = "Elimina una tienda específica por su ID")
    public ResponseEntity<Void> eliminarTienda(@PathVariable Long id) {
        try {
            tiendaService.eliminarTienda(id);
            return ResponseEntity.noContent().build(); // Retorna 204 No Content si se elimina correctamente
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // Retorna 404 Not Found si no se encuentra la tienda
        }
    }

}