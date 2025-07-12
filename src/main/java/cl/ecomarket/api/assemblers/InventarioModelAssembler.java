package cl.ecomarket.api.assemblers;

import cl.ecomarket.api.controller.InventarioControllerV2;
import cl.ecomarket.api.model.Inventario;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class InventarioModelAssembler implements RepresentationModelAssembler<Inventario, EntityModel<Inventario>> {

    @Override
    public EntityModel<Inventario> toModel(Inventario inventario) {
        return EntityModel.of(inventario,
            linkTo(methodOn(InventarioControllerV2.class).obtenerPorIdTienda(inventario.getTienda().getId()))
                .withSelfRel()
                .withType("GET"),
            linkTo(methodOn(InventarioControllerV2.class).listarTodos())
                .withRel("todos-los-inventarios")
                .withType("GET"),
            linkTo(methodOn(InventarioControllerV2.class).eliminarInventario(inventario.getId()))
                .withRel("eliminar")
                .withType("DELETE")
        );
    }
}
