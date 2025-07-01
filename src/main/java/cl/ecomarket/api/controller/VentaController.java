package cl.ecomarket.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.ecomarket.api.model.Estados;
import cl.ecomarket.api.model.Venta;
import cl.ecomarket.api.services.VentaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("api/v1/ventas")
@Tag(name = "Ventas", description = "Operaciones relacionadas con las ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @PostMapping
    @Operation(summary = "Registrar una nueva venta", description = "Crea una nueva venta en el sistema")
    public ResponseEntity<Venta> registrarVenta(@RequestBody Venta venta,@RequestParam Estados estado) {
        try {
            Venta nuevaVenta = ventaService.generarVenta(venta);
            return ResponseEntity.status(201).body(nuevaVenta); // 201 Created
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request
        }

    }

    @GetMapping
    @Operation(summary = "Obtener todas las ventas", description = "Retorna una lista de todas las ventas registradas")
    public ResponseEntity<List<Venta>> obtenerTodas() {
        try {
            List<Venta> ventas = ventaService.obtenerTodas();
            if (ventas.isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 No Content
            }
            return ResponseEntity.ok(ventas); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // 500 Internal Server Error
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener venta por ID", description = "Retorna una venta específica por su ID")
    public ResponseEntity<Venta> obtenerPorId(@PathVariable Long id) {
        try {
            Venta venta = ventaService.obtenerPorId(id);
            return ResponseEntity.ok(venta); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    @GetMapping("/clienteId/{clienteId}")
    @Operation(summary = "Obtener ventas por cliente ID", description = "Retorna una lista de ventas asociadas a un cliente específico")
    public ResponseEntity<List<Venta>> obtenerPorCliente(@PathVariable Long clienteId) {
        try {
            List<Venta> ventas = ventaService.obtenerPorClienteID(clienteId);
            if (ventas.isEmpty()) {
                return ResponseEntity.noContent().build(); // 204 No Content
            }
            return ResponseEntity.ok(ventas); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    @GetMapping("/factura/{id}")
    @Operation(summary = "Generar factura de una venta", description = "Genera una factura para una venta específica")
    public ResponseEntity<String> generarFactura(@PathVariable Long id) {
        try {
            Venta venta = ventaService.obtenerPorId(id);
            if (venta == null) {
                return ResponseEntity.notFound().build(); // 404 Not Found
            }
            String factura = ventaService.generarFactura(venta.getId());
            return ResponseEntity.ok(factura); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.status(500).build(); // 500 Internal Server Error
        }
    }

    @DeleteMapping("eliminar/{id}")
    @Operation(summary = "Eliminar una venta", description = "Elimina una venta específica por su ID")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Long id) {
        try {
            ventaService.eliminarVenta(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}
