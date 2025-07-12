package cl.ecomarket.api.controller;

import cl.ecomarket.api.assemblers.InventarioModelAssembler;
import cl.ecomarket.api.model.Inventario;
import cl.ecomarket.api.services.InventarioService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/inventario")
@Tag(name = "Inventario V2", description = "Operaciones relacionadas con el inventario de productos en las tiendas con enlaces HATEOAS")
public class InventarioControllerV2 {

    @Autowired
    private InventarioService inventarioService;

    @Autowired
    private InventarioModelAssembler assembler;

    @GetMapping("/tiendas")
    @Operation(summary = "Listar todos los inventarios", description = "Obtiene una lista de todos los inventarios de productos en las tiendas")
    public ResponseEntity<CollectionModel<EntityModel<Inventario>>> listarTodos() {
        List<Inventario> inventarios = inventarioService.listarTodos();
        if (inventarios.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        List<EntityModel<Inventario>> inventariosModel = inventarios.stream()
            .map(assembler::toModel)
            .collect(Collectors.toList());
        return ResponseEntity.ok(
            CollectionModel.of(inventariosModel,
                linkTo(methodOn(InventarioControllerV2.class).listarTodos()).withSelfRel())
        );
    }

    @GetMapping("/tienda/{tiendaId}")
    @Operation(summary = "Listar productos por ID de tienda", description = "Obtiene una lista de productos inventariados en una tienda específica")
    public ResponseEntity<EntityModel<Inventario>> obtenerPorIdTienda(@PathVariable Long tiendaId) {
        try {
            Inventario inventario = inventarioService.encontrarPorTienda(tiendaId);
            return ResponseEntity.ok(assembler.toModel(inventario));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Guardar un nuevo inventario", description = "Crea un nuevo inventario de productos en una tienda")
    public ResponseEntity<EntityModel<Inventario>> guardarInventario(@RequestBody Inventario inventario) {
        try {
            Inventario nuevoInventario = inventarioService.crearInventario(inventario);
            return ResponseEntity.status(HttpStatus.CREATED).body(assembler.toModel(nuevoInventario));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PatchMapping
    @Operation(summary = "Actualizar stock de un producto en una tienda", description = "Actualiza el stock de un producto específico en una tienda")
    public ResponseEntity<EntityModel<Inventario>> actualizarStock(@RequestParam Long tiendaId,
                                                                   @RequestParam Long productoId,
                                                                   @RequestParam String operacion,
                                                                   @RequestParam int stock) {
        try {
            Inventario inventario = inventarioService.actualizarStock(tiendaId, productoId, stock, operacion);
            return ResponseEntity.ok(assembler.toModel(inventario));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
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
