package cl.ecomarket.api.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import cl.ecomarket.api.model.Estados;
import cl.ecomarket.api.model.Venta;
import cl.ecomarket.api.services.VentaService;

@RestController
@RequestMapping("api/v1/ventas")
public class VentaController {

    @Autowired
    private VentaService ventaService; 

    @PostMapping
    public ResponseEntity<Venta> registrarVenta(@RequestBody Venta venta,@RequestParam Estados estado) {
        try {
            Venta nuevaVenta = ventaService.generarVenta(venta);
            return ResponseEntity.status(201).body(nuevaVenta); // 201 Created
        } catch (Exception e) {
            return ResponseEntity.badRequest().build(); // 400 Bad Request
        }

    }

    @GetMapping
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
    public ResponseEntity<Venta> obtenerPorId(@PathVariable Long id) {
        try {
            Venta venta = ventaService.obtenerPorId(id);
            return ResponseEntity.ok(venta); // 200 OK
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }

    @GetMapping("/clienteId/{clienteId}")
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
    public ResponseEntity<Void> eliminarVenta(@PathVariable Long id) {
        try {
            ventaService.eliminarVenta(id);
            return ResponseEntity.noContent().build(); // 204 No Content
        } catch (Exception e) {
            return ResponseEntity.notFound().build(); // 404 Not Found
        }
    }
}
