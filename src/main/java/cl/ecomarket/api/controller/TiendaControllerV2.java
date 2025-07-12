package cl.ecomarket.api.controller;

import cl.ecomarket.api.assemblers.TiendaModelAssembler;
import cl.ecomarket.api.model.Tienda;
import cl.ecomarket.api.services.TiendaService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
@RequestMapping("/api/v2/tiendas")
@Tag(name = "Tiendas V2", description = "Operaciones relacionadas con las tiendas con HATEOAS")
public class TiendaControllerV2 {

    @Autowired
    private TiendaService tiendaService;

    @Autowired
    private TiendaModelAssembler assembler;

    @GetMapping
    @Operation(summary = "Listar todas las tiendas", description = "Obtiene una lista de todas las tiendas con enlaces HATEOAS")
    public ResponseEntity<CollectionModel<EntityModel<Tienda>>> obtenerTodasLasTiendas() {
        List<Tienda> tiendas = tiendaService.obtenerTodasLasTiendas();
        if (tiendas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        List<EntityModel<Tienda>> tiendasConLinks = tiendas.stream()
                .map(assembler::toModel)
                .toList();

        return ResponseEntity.ok(
                CollectionModel.of(tiendasConLinks,
                        linkTo(methodOn(TiendaControllerV2.class).obtenerTodasLasTiendas()).withSelfRel())
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener tienda por ID", description = "Obtiene una tienda específica por su ID con enlaces HATEOAS")
    public ResponseEntity<EntityModel<Tienda>> obtenerTiendaPorId(@PathVariable Long id) {
        try {
            Tienda tienda = tiendaService.obtenerTiendaPorId(id);
            return ResponseEntity.ok(assembler.toModel(tienda));
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping
    @Operation(summary = "Crear una nueva tienda", description = "Crea una nueva tienda en el sistema y devuelve su representación con enlaces HATEOAS")
    public ResponseEntity<EntityModel<Tienda>> crearTienda(@RequestBody Tienda tienda) {
        try {
            Tienda nueva = tiendaService.guardarTienda(tienda);
            return ResponseEntity
                    .created(linkTo(methodOn(TiendaControllerV2.class).obtenerTiendaPorId(nueva.getId())).toUri())
                    .body(assembler.toModel(nueva));
        } catch (Exception e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarTienda(@PathVariable Long id) {
        try {
            tiendaService.eliminarTienda(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
}
