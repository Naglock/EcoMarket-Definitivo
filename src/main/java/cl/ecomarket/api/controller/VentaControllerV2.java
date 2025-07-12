package cl.ecomarket.api.controller;

import cl.ecomarket.api.assemblers.VentaModelAssembler;
import cl.ecomarket.api.model.Venta;
import cl.ecomarket.api.model.Estados;
import cl.ecomarket.api.services.VentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

import java.util.List;
import java.util.stream.Collectors;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/ventas")
@Tag(name = "Ventas V2", description = "Operaciones HATEOAS para ventas")
public class VentaControllerV2 {

    @Autowired
    private VentaService ventaService;

    @Autowired
    private VentaModelAssembler assembler;

    @PostMapping
    @Operation(summary = "Registrar una nueva venta", description = "Registra una nueva venta y devuelve su representación con enlaces HATEOAS")
    public ResponseEntity<EntityModel<Venta>> registrarVenta(@RequestBody Venta venta, @RequestParam Estados estado) {
        try {
            Venta nuevaVenta = ventaService.generarVenta(venta);
            EntityModel<Venta> entityModel = assembler.toModel(nuevaVenta);
            return ResponseEntity
                    .created(linkTo(methodOn(VentaControllerV2.class).obtenerPorId(nuevaVenta.getId())).toUri())
                    .body(entityModel);
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @GetMapping
    @Operation(summary = "Obtener todas las ventas", description = "Obtiene una lista de todas las ventas registradas con enlaces HATEOAS")
    public ResponseEntity<CollectionModel<EntityModel<Venta>>> obtenerTodas() {
        try {
            List<EntityModel<Venta>> ventas = ventaService.obtenerTodas().stream()
                    .map(assembler::toModel)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    CollectionModel.of(ventas,
                            linkTo(methodOn(VentaControllerV2.class).obtenerTodas()).withSelfRel())
            );
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener venta por ID", description = "Obtiene una venta específica por su ID con enlaces HATEOAS")
    public ResponseEntity<EntityModel<Venta>> obtenerPorId(@PathVariable Long id) {
        try {
            Venta venta = ventaService.obtenerPorId(id);
            return ResponseEntity.ok(assembler.toModel(venta));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/clienteId/{clienteId}")
    @Operation(summary = "Obtener ventas por ID de cliente", description = "Obtiene una lista de ventas asociadas a un cliente específico por su ID con enlaces HATEOAS")
    public ResponseEntity<CollectionModel<EntityModel<Venta>>> obtenerPorCliente(@PathVariable Long clienteId) {
        try {
            List<EntityModel<Venta>> ventas = ventaService.obtenerPorClienteID(clienteId).stream()
                    .map(assembler::toModel)
                    .collect(Collectors.toList());

            return ResponseEntity.ok(
                    CollectionModel.of(ventas,
                            linkTo(methodOn(VentaControllerV2.class).obtenerPorCliente(clienteId)).withSelfRel())
            );
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/factura/{id}")
    @Operation(summary = "Generar factura para venta")
    public ResponseEntity<String> generarFactura(@PathVariable Long id) {
        try {
            String factura = ventaService.generarFactura(id);
            return ResponseEntity.ok(factura);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/eliminar/{id}")
    @Operation(summary = "Eliminar venta")
    public ResponseEntity<Void> eliminarVenta(@PathVariable Long id) {
        try {
            ventaService.eliminarVenta(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
